public class SlowCalculator implements Runnable {

    private final long N;
    private volatile boolean interrupted = false;
    private volatile boolean completed = false;
    private Integer result = null;

    public SlowCalculator(final long N) {
        this.N = N;
    }

    public void run() {
        try {
            this.result = calculateNumFactors(N);
            completed = true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            interrupted = true;
        }
    }

    private int calculateNumFactors(final long N) throws InterruptedException { //this was changed to static
        int count = 0;
        for (long candidate = 2; candidate <= Math.abs(N); ++candidate) {
            if (interrupted || Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("Calculation was interrupted.");
            }

            if (isPrime(candidate)) {
                if (Math.abs(N) % candidate == 0) {
                    count++;
                }
            }
        }
        return count;
    }

    private static boolean isPrime(final long n) {
        for (long candidate = 2; candidate < Math.sqrt(n) + 1; ++candidate) {
            if (n % candidate == 0) {
                return false;
            }
        }
        return true;
    }

    public synchronized Integer getResult() {
        return result;
    }
}

