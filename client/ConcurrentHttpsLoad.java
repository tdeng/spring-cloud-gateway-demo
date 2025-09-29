import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ConcurrentHttpsLoad {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java ConcurrentHttpsLoad <url> <concurrency>");
            System.exit(2);
        }

        String url = args[0];
        int concurrency = Integer.parseInt(args[1]);

        System.out.println("Target: " + url);
        System.out.println("Concurrency: " + concurrency);
        runLoad(url, concurrency);
    }

    private static void runLoad(String url, int concurrency) throws Exception {
        // Executor for HttpClient and blocking waits
        ExecutorService executor = Executors.newFixedThreadPool(concurrency);

        // Create an SSLContext that trusts all certs
        SSLContext sslContext = createInsecureSSLContext();

        // Disable hostname verification via SSLParameters
        SSLParameters sslParameters = new SSLParameters();
        sslParameters.setEndpointIdentificationAlgorithm(null); // disable hostname verification

        HttpClient client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .sslParameters(sslParameters)
                .connectTimeout(Duration.ofSeconds(10))
                .executor(executor)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        AtomicInteger success = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();
        List<CompletableFuture<Void>> futures = new ArrayList<>(concurrency);

        Instant start = Instant.now();

        for (int i = 0; i < concurrency; i++) {
            CompletableFuture<Void> f = client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .orTimeout(60, TimeUnit.SECONDS)
                .thenAccept(resp -> {
                    int status = resp.statusCode();
                    if (status >= 200 && status < 300) {
                        success.incrementAndGet();
                    } else {
                        failed.incrementAndGet();
                    }
                })
                .exceptionally(ex -> {
                    failed.incrementAndGet();
                    // Uncomment for debugging:
                    // ex.printStackTrace();
                    return null;
                });

            futures.add(f);
        }

        // Wait for all to complete
        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            all.get(120, TimeUnit.SECONDS); // overall timeout
        } catch (TimeoutException te) {
            System.err.println("Timeout waiting for requests to finish.");
        } finally {
            executor.shutdownNow();
        }

        Instant end = Instant.now();
        long tookMs = Duration.between(start, end).toMillis();

        System.out.println("Finished. Time(ms): " + tookMs);
        System.out.println("Success: " + success.get());
        System.out.println("Failed : " + failed.get());
    }

    private static SSLContext createInsecureSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAll = new TrustManager[] {
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[0]; }
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
            }
        };

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAll, new java.security.SecureRandom());
        return sc;
    }
}