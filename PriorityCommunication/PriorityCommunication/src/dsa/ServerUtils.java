package dsa;

import java.util.Random;

public class ServerUtils {

    private static final Random random = new Random();

    public synchronized static void waitForRandomRange(int startRange, int endRange) {
        try {
            if (endRange - startRange < 0) {
                throw new IllegalArgumentException("Start must be greater or equal to end");
            } else if (endRange == startRange) {
                Thread.sleep(startRange);
            } else {
                Thread.sleep(random.nextInt(endRange - startRange) + startRange);
            }
        } catch (InterruptedException exception) {
            System.err.println("Cannot sleep thread.");
            exception.printStackTrace();
        }
    }
}
