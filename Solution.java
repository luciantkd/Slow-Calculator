
import java.util.concurrent.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Solution implements CommandRunner{

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Map<Long, SlowCalculator> calculators = new ConcurrentHashMap<>();
    private final Map<Long, Future<Integer>> futures = new ConcurrentHashMap<>();
    private final Map<Long, Long> scheduledAfter = new ConcurrentHashMap<>();

    public Solution() {
    }

    private String start(long N) {
        if (futures.containsKey(N)) {
            return "calculation for " + N + " is already running";
        }
        SlowCalculator calculator = new SlowCalculator(N);
        Future<Integer> future = executorService.submit(() -> {
            Thread.currentThread().setName("Calculator-" + N);
            calculator.run();
            return calculator.getResult();
        });
        futures.put(N, future);
        calculators.put(N, calculator);
        return "started " + N;
    }

    private String cancel(long N) {
        Future<Integer> future = futures.get(N);
        if (future != null) {
            future.cancel(true);
            calculators.remove(N);
            startScheduledAfter(N);
            return "cancelled " + N;
        }
        return N + " was not running";
    }

    private String running() {
        List<Long> runningTasks = futures.entrySet().stream()
                .filter(entry -> !entry.getValue().isDone())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        if (runningTasks.isEmpty()) {
            return "no calculations running";
        }
        return runningTasks.size() + " calculations running: " + runningTasks.stream().map(Object::toString).collect(Collectors.joining(" "));
    }


    private String get(long N) {
        Future<Integer> future = futures.get(N);
        if (future != null) {
            if (future.isDone()) {
                try {
                    if (future.isCancelled()) {
                        return "cancelled";
                    }
                    Integer result = future.get();
                    return result != null ? "result is " + result : "cancelled";
                } catch (CancellationException e) {
                    return "cancelled";
                } catch (InterruptedException | ExecutionException e) {
                    Thread.currentThread().interrupt();
                    return "calculation for " + N + " was interrupted";
                }
            } else {
                return "calculating";
            }
        }
        return "No calculation found for " + N;
    }

    private String after(long N, long M) {
        if (!futures.containsKey(N)) {
            return "No calculation found for " + N;
        }
        scheduledAfter.put(N, M);
        return M + " will start after " + N;
    }

    private String finish() {
        futures.values().forEach(future -> {
            try {
                future.get();
            } catch (CancellationException e) {

            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
            }
        });
        return "finished";
    }

    private String abort() {
        futures.forEach((N, future) -> future.cancel(true));
        futures.clear();
        calculators.clear();
        scheduledAfter.clear();
        return "aborted";
    }

    private void startScheduledAfter(long N) {
        Long M = scheduledAfter.remove(N);
        if (M != null) {
            start(M);
        }
    }

    private boolean isValidLong(String input) {
        try {
            Long.parseLong(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String runCommand(String command) {
        String[] parts = command.trim().split("\\s+");
        if (parts.length < 1) {
            return "Invalid command";
        }
        try {
            switch (parts[0]) {
                case "start":
                    if (parts.length != 2 || !isValidLong(parts[1])) return "Invalid command";
                    return start(Long.parseLong(parts[1]));
                case "cancel":
                    if (parts.length != 2 || !isValidLong(parts[1])) return "Invalid command";
                    return cancel(Long.parseLong(parts[1]));
                case "running":
                    if (parts.length != 1) return "Invalid command";
                    return running();
                case "get":
                    if (parts.length != 2 || !isValidLong(parts[1])) return "Invalid command";
                    return get(Long.parseLong(parts[1]));
                case "after":
                    if (parts.length != 3 || !isValidLong(parts[1]) || !isValidLong(parts[2])) return "Invalid command";
                    return after(Long.parseLong(parts[1]), Long.parseLong(parts[2]));
                case "finish":
                    if (parts.length != 1) return "Invalid command";
                    return finish();
                case "abort":
                    if (parts.length != 1) return "Invalid command";
                    return abort();
                default:
                    return "Invalid command";
            }
        } catch (NumberFormatException e) {
            return "Invalid command";
        }
    }
}