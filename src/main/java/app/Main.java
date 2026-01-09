package app;

import app.store.HazelcastStore;
import app.store.MongoStore;
import app.store.RedisStore;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Main {

    // Store'ları static tanımlayalım ki handler içinden erişebilelim
    static RedisStore redisStore;
    static HazelcastStore hazelcastStore;
    static MongoStore mongoStore;

    public static void main(String[] args) throws IOException {
        // 1. Veritabanlarını başlat
        System.out.println("Veritabanları başlatılıyor...");
        redisStore = new RedisStore();
        hazelcastStore = new HazelcastStore();
        mongoStore = new MongoStore();

        // 2. HTTP Sunucusunu başlat (Port 8080)
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);

        // 3. Endpointleri tanımla
        server.createContext("/nosql-lab-rd", new RedisHandler());
        server.createContext("/nosql-lab-hz", new HazelcastHandler());
        server.createContext("/nosql-lab-mon", new MongoHandler());

        server.setExecutor(null); // default executor
        System.out.println("Sunucu çalışıyor: http://localhost:8080");
        server.start();
    }

    // Ortak Yardımcı Metot: URL'den student_no'yu parse etme
    private static String getStudentIdFromQuery(String query) {
        if (query != null && query.startsWith("student_no=")) {
            return query.split("=")[1];
        }
        return null;
    }

    // Ortak Yardımcı Metot: Yanıtı Gönderme
    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        if (response == null) {
            String msg = "Ogrenci bulunamadi";
            exchange.sendResponseHeaders(404, msg.length());
            OutputStream os = exchange.getResponseBody();
            os.write(msg.getBytes());
            os.close();
        } else {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // --- Handlers ---

    static class RedisHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // URL yapısı: /nosql-lab-rd/student_no=12345
            // URI path'in son kısmını almalıyız çünkü query string değil path param gibi verilmiş örnekte.
            // Örnek: localhost:8080/nosql-lab-rd/student_no=xxxxxxxxxx

            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            String lastPart = parts[parts.length - 1]; // student_no=xxxx

            String studentId = getStudentIdFromQuery(lastPart);
            String result = redisStore.getStudent(studentId);
            sendResponse(exchange, result);
        }
    }

    static class HazelcastHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String lastPart = path.substring(path.lastIndexOf("/") + 1);

            String studentId = getStudentIdFromQuery(lastPart);
            String result = hazelcastStore.getStudent(studentId);
            sendResponse(exchange, result);
        }
    }

    static class MongoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String lastPart = path.substring(path.lastIndexOf("/") + 1);

            String studentId = getStudentIdFromQuery(lastPart);
            String result = mongoStore.getStudent(studentId);
            sendResponse(exchange, result);
        }
    }
}
