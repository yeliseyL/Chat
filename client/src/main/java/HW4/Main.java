package HW4;

public class Main {
    private volatile char currentLetter = 'A';

    public static void main(String[] args) {
        Main w = new Main();
        Thread t1 = new Thread(w::printA);
        Thread t2 = new Thread(w::printB);
        Thread t3 = new Thread(w::printC);
        t1.start();
        t2.start();
        t3.start();
    }

    synchronized public void printA() {
        try {
            for (int i = 0; i < 5; i++) {
                while (currentLetter != 'A') {
                    wait();
                }
                System.out.print("A");
                currentLetter = 'B';
                notifyAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized public void printB() {
        try {
            for (int i = 0; i < 5; i++) {
                while (currentLetter != 'B') {
                    wait();
                }
                System.out.print("B");
                currentLetter = 'C';
                notifyAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized private void printC() {
        try {
            for (int i = 0; i < 5; i++) {
                while (currentLetter != 'C') {
                    wait();
                }
                System.out.print("C");
                currentLetter = 'A';
                notifyAll();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
