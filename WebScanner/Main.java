public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<String> urls = List.of(
            "https://google.com",
            "https://youtube.com",
            "https://github.com",
            "https://nonexistent-site.com"
        );

        WebScanner scanner = new WebScanner(urls, 4); // 4 потока
        scanner.scan();
    }
}