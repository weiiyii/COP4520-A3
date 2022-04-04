import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Semaphore;

public class presentParty {

    public final int maxVal = 500000;
    public AtomicInteger presentInChain = new AtomicInteger(0);
    public AtomicInteger noteCounter = new AtomicInteger(0);
    public Node presentChain = new Node(-1);
    public List<servantThread> threads;

    public static void main(String[] args) {
        
        presentParty program = new presentParty();
        program.threads = new ArrayList<>();

        for(int i=0; i<4; i++){
            servantThread th = new servantThread(program, i);
            program.threads.add(th);
            
        }

        for(servantThread th: program.threads){
            th.start();
        }

        for(servantThread th: program.threads){
            try{
                th.join();
            }
            catch(Exception e){
                System.out.println(e);
            }
        }

    }
}

class servantThread extends Thread {

    enum servantStatus {
        ADD_GIFT, WRITE_NOTE, CHECK_GIFT
    }

    public presentParty program;
    public int id;
    public servantStatus status;

    public servantThread(presentParty p, int id){
        this.program = p;
        this.id = id;
        this.status = servantStatus.ADD_GIFT;
    }

    public void setStatus(servantStatus s){
        this.status = s;
        
    }

    @Override
    public void run(){
        
        while(program.noteCounter.get() < program.maxVal){
            try{

                Thread.sleep(1);
            }
            catch(Exception e){
                // System.out.println(e);
                break;
            }
            
            if(Math.random()<0.1) {
                this.setStatus(servantStatus.CHECK_GIFT);
            }

            
            if(this.status == servantStatus.ADD_GIFT){
                
                // Enqueue present list, increment present counter
                int presentToAdd = (int)Math.floor(Math.random()*program.maxVal);

                while(true){
                    Node pred = program.presentChain;
                    Node curr = pred.next;

                    while(curr!=null && curr.id<presentToAdd) {
                        
                        pred = curr;
                        curr = curr.next;
                    }

                    while(!pred.lock.tryAcquire()) {}
                    try {
                        if(curr!=null) {
                            while(!curr.lock.tryAcquire()) {}
                        }
                        try {
                            // ADD: validate pred !deleted, curr !deleted, pred.next == curr
                            if(!pred.deleted && (curr==null || !curr.deleted) && pred.next==curr) {
                                if(curr!=null && curr.id==presentToAdd) {
                                    // System.out.println("Unable to add: Present #" + presentToAdd + " is already in the chain");
                                    break;
                                }

                                Node e = new Node(presentToAdd);
                                e.next = curr;
                                pred.next = e;
                                program.presentInChain.incrementAndGet();
                                System.out.println("Add present #" + presentToAdd + " to present chain");
                                break;
                            }
                        } finally {
                            if(curr!=null) {
                                curr.lock.release();
                            }
                        }
                    } finally {
                        pred.lock.release();
                    }
                }

                this.setStatus(servantStatus.WRITE_NOTE);
                
                
            }
            else if(this.status == servantStatus.WRITE_NOTE) {
                // Dequeue present list, increment noteCounter
                int presentToRemove = (int)Math.floor(Math.random()*program.maxVal);

                while(true){
                    Node pred = program.presentChain;
                    Node curr = pred.next;

                    while(curr!=null && curr.id<presentToRemove) {
                        
                        pred = curr;
                        curr = curr.next;
                    }

                    if(curr==null) {
                        // System.out.println("Unable to remove: Present #" + presentToRemove + " is not in the chain");
                        break;
                    }

                    while(!pred.lock.tryAcquire()) {}

                    try {
                        while(!curr.lock.tryAcquire()) {}

                        try {
                            // REMOVE: validate pred !deleted, curr !deleted, pred.next == curr
                            if(!pred.deleted && !curr.deleted && pred.next==curr) {
                                if(curr.id!=presentToRemove) {
                                    // System.out.println("Unable to remove: Present #" + presentToRemove + " is not in the chain");
                                    break;
                                }

                                curr.deleted = true;
                                program.presentInChain.decrementAndGet();
                                program.noteCounter.incrementAndGet();
                                // System.out.println("Logical remove present #" + presentToRemove);
                                System.out.println("Servant #" + this.id + " writes thank you note for present #" + presentToRemove);
                                pred.next = curr.next;
                                // System.out.println("Physical remove present #" + presentToRemove);
                                break;
                            }
                            
                        } finally {
                            curr.lock.release();
                        }
                    } finally {
                        pred.lock.release();
                    }
                }

                this.setStatus(servantStatus.ADD_GIFT);
            }
            else if(this.status == servantStatus.CHECK_GIFT) {
                // Generate a random num between 0 - 499999, loop through present list and check if it is present
                int presentToCheck = (int)Math.floor(Math.random()*program.maxVal);

                Node temp = program.presentChain;
                while(temp!=null && temp.id<presentToCheck) {
                    temp = temp.next;
                }
                if(temp!=null && temp.id==presentToCheck && !temp.deleted) {
                    System.out.println("Present #" + presentToCheck + " is in the present chain!");
                }
                else {
                    System.out.println("Present #" + presentToCheck + " is not in the present chain");
                }

                // Set status back, 50% chance of each
                if(Math.random() < 0.5) {
                    this.setStatus(servantStatus.ADD_GIFT);
                }
                else {
                    this.setStatus(servantStatus.WRITE_NOTE);
                }
            }
        }

    }
}

class Node {
    public int id;
    public boolean deleted;
    public Semaphore lock;
    public Node next;

    Node(int id) {
        this.id = id;
        this.deleted = false;
        this.lock = new Semaphore(1);
        this.next = null;
    }


}