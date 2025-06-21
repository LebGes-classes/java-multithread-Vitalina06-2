import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class WebScannerTest {
    @Test
    void testScanSuccess() throws InterruptedException {
        List<String> testUrls = List.of(
            "https://google.com",
            "https://github.com"
        );
        WebScanner scanner = new WebScanner(testUrls, 2);
        scanner.scan(); // Проверяем, что не выбрасывает исключений
    }

    @Test
    void testScanFailedUrl() throws InterruptedException {
        List<String> testUrls = List.of("https://nonexistent-site-12345.com");
        WebScanner scanner = new WebScanner(testUrls, 1);
        scanner.scan(); // Должен обработать ошибку соединения
    }
}