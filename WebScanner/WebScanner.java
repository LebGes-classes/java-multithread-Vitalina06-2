import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.*;

public class WebScanner {
    private final ExecutorService executorService;
    private final List<String> urls;

    public WebScanner(List<String> urls, int threads) {
        this.urls = urls;
        this.executorService = Executors.newFixedThreadPool(threads);
    }

    public void scan() throws InterruptedException {
        List<Future<ScanResult>> futures = urls.stream()
            .map(url -> executorService.submit(() -> scanUrl(url)))
            .toList();

        for (Future<ScanResult> future : futures) {
            try {
                ScanResult result = future.get();
                System.out.printf("[%s] %s - %dms (Status: %d)%n",
                    result.success() ? "OK" : "FAIL",
                    result.url(),
                    result.responseTime(),
                    result.statusCode());
            } catch (ExecutionException e) {
                System.err.println("Error scanning URL: " + e.getCause().getMessage());
            }
        }
        executorService.shutdown();
    }

    private ScanResult scanUrl(String url) {
        long startTime = System.currentTimeMillis();
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            int statusCode = connection.getResponseCode();
            long responseTime = System.currentTimeMillis() - startTime;
            return new ScanResult(url, statusCode, responseTime, true);
        } catch (IOException e) {
            return new ScanResult(url, -1, System.currentTimeMillis() - startTime, false);
        }
    }

    public record ScanResult(
        String url,
        int statusCode,
        long responseTime,
        boolean success
    ) {}
}