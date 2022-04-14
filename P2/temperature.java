import java.util.*;
import java.util.concurrent.Semaphore;

public class temperature {

    public static void main(String[] args) {
        
        // 10 hours in tota`l
        for(int i=0; i<10; i++) {
            
            memoryStorage program = new memoryStorage();

            List<sensorThread> threads = new ArrayList<>();
            // 8 threads in total
            for(int j=0; j<8; j++) {
                sensorThread th = new sensorThread(program, j);
                threads.add(th);
                th.start();
            }

            for(sensorThread th: threads){
                try{
                    th.join();
                }
                catch(Exception e){
                    System.out.println(e);
                }
            }

            System.out.println("===============================================================================");
            System.out.println("Current Hour: " + i);
            System.out.println("Top 5 highest temperature: " + program.max);
            System.out.println("Top 5 lowest temperature: " + program.min);
            System.out.printf("Largest temperature difference of %dF was observed from %d to %d minute\n", program.maxDiff, program.intervalStart, program.intervalEnd);
            System.out.println("===============================================================================\n");
            
        }

    }
}

class memoryStorage {

    public PriorityQueue<Integer> max;
    public PriorityQueue<Integer> min;
    public int intervalMax;
    public int intervalMin;
    public Semaphore lock;
    public int maxDiff;
    public int intervalEnd, intervalStart;


    public memoryStorage() {

        max = new PriorityQueue<>();
        min = new PriorityQueue<>((a, b)-> (b-a));
        intervalMax = Integer.MIN_VALUE;
        intervalMin = Integer.MAX_VALUE;
        lock = new Semaphore(1);
        maxDiff = Integer.MIN_VALUE;

    }

    public void storeData(int temp, int minute) {

        while(!lock.tryAcquire()) {}
        try {

            // update temperature list
            max.add(temp);
            if(max.size()>5) max.poll();

            min.add(temp);
            if(min.size()>5) min.poll();

            // update max temperature diff interval
            intervalMax = Math.max(intervalMax, temp);
            intervalMin = Math.min(intervalMin, temp);

            // an interval ends
            if(minute%10==9) {
                int curDiff = intervalMax - intervalMin;
                if(maxDiff < curDiff) {
                    maxDiff = curDiff;
                    intervalEnd = minute;
                    intervalStart = intervalEnd-9;
                }
                intervalMax = Integer.MIN_VALUE;
                intervalMin = Integer.MAX_VALUE;
            }

        } finally {
            lock.release();
        }

    }
}

class sensorThread extends Thread {

    public memoryStorage program;
    public int id;

    public sensorThread(memoryStorage program, int id) {

        this.program = program;
        this.id = id;
    }


    @Override
    public void run() {

        for(int minute=0; minute<60; minute++) {
            // pause for a sec
            try {
                Thread.sleep(1);
            } catch(Exception e) {
                System.out.println(e);
            }

            int temp = readTemp();

            program.storeData(temp, minute);
        }

    }

    public int readTemp() {
        return (int)(Math.random() * 171) - 100;
    }
}