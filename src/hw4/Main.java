package hw4;

class PrintLetterThread extends Thread {
    private static char currentLetter = 'A';
    private static Object mon = new Object();
    private int cnt = 0;
    char letter;

    PrintLetterThread(char letter) {
        super();
        this.letter = letter;
    }

    @Override
    public void run() {
        synchronized (mon) {
            while (cnt < 5) {
                while (currentLetter != letter) {
                    try {
                        mon.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.print(letter);
                cnt++;
                nextLetter();
                mon.notifyAll();
            }
        }
    }

    private void nextLetter() {
        if (currentLetter == 'C') {
            currentLetter = 'A';
        } else {
            currentLetter++;
        }
    }
}

public class Main {
    public static void main(String[] args) {
        new PrintLetterThread('C').start();
        new PrintLetterThread('B').start();
        new PrintLetterThread('A').start();
    }

}
