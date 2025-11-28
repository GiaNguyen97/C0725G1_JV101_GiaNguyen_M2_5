package demo_chatbox_AI_gemini;

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.google.gson.*;

/**
 * 💬 GeminiChatBot
 * ----------------
 * Chatbot console sử dụng API Gemini (Google Generative Language).
 * Ghi nhớ lịch sử hội thoại vào file JSON, có lưu thời gian và lệnh 'clear' để xoá.
 */
public class GeminiChatBot {

    private static final String MODEL = ConfigReader.get("MODEL");
    private static final String API_KEY = ConfigReader.get("API_KEY");

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1/models/"
                    + MODEL + ":generateContent?key=" + API_KEY;

    private static final String HISTORY_FILE = "E:\\CODEGYM\\bai_tap_code_gym\\module_2_5\\src\\main\\java\\demo_chatbox_AI_gemini\\conversation.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /** Định dạng thời gian chuẩn */
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HttpClient client = HttpClient.newHttpClient();

        System.out.println("🤖 Gemini Chatbot đã sẵn sàng!");
        System.out.println("Gõ 'exit' để thoát, 'clear' để xóa lịch sử.\n");

        List<Map<String, Object>> history = loadConversationHistory();

        while (true) {
            System.out.print("👤 Bạn: ");
            String input = scanner.nextLine().trim();

            // --- Lệnh đặc biệt ---
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("👋 Tạm biệt!");
                break;
            }
            if (input.equalsIgnoreCase("clear")) {
                clearConversationHistory();
                history.clear();
                System.out.println("🧹 Lịch sử trò chuyện đã được xóa!\n");
                continue;
            }

            // --- Gửi câu hỏi ---
            System.out.println("... ⏳ Đang chờ phản hồi từ Gemini ...");

            String response = callGeminiAPI(client, input, history);
            String answer = parseGeminiResponse(response);

            System.out.println("🤖 Gemini: " + answer + "\n");

            // --- Lưu lịch sử ---
            saveConversationHistory(input, answer);
            history.add(Map.of(
                    "role", "user",
                    "content", input,
                    "timestamp", LocalDateTime.now().format(TIME_FORMAT)
            ));
            history.add(Map.of(
                    "role", "assistant",
                    "content", answer,
                    "timestamp", LocalDateTime.now().format(TIME_FORMAT)
            ));
        }

        scanner.close();
    }

    /** 📨 Gửi yêu cầu đến Gemini API (kèm lịch sử hội thoại). */
    private static String callGeminiAPI(HttpClient client, String userInput, List<Map<String, Object>> history) {
        try {
            List<Map<String, Object>> contents = new ArrayList<>();
            for (Map<String, Object> msg : history) {
                contents.add(Map.of(
                        "role", msg.get("role").equals("user") ? "user" : "model",
                        "parts", List.of(Map.of("text", msg.get("content")))
                ));
            }
            contents.add(Map.of("role", "user", "parts", List.of(Map.of("text", userInput))));

            String requestBody = gson.toJson(Map.of("contents", contents));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();

        } catch (IOException | InterruptedException e) {
            return "{\"error\": {\"message\": \"Không thể kết nối API: " + e.getMessage() + "\"}}";
        }
    }

    /** 🧠 Phân tích JSON phản hồi để lấy phần text. */
    private static String parseGeminiResponse(String responseBody) {
        try {
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

            if (json.has("candidates")) {
                JsonObject first = json.getAsJsonArray("candidates").get(0).getAsJsonObject();
                JsonArray parts = first.getAsJsonObject("content").getAsJsonArray("parts");
                return parts.get(0).getAsJsonObject().get("text").getAsString();
            } else if (json.has("error")) {
                return "❌ Lỗi API: " + json.getAsJsonObject("error").get("message").getAsString();
            } else {
                return "❌ Lỗi phản hồi: Không có 'candidates' hoặc 'error'.";
            }
        } catch (Exception e) {
            return "❌ Lỗi phân tích JSON: " + e.getMessage();
        }
    }

    /** 💾 Lưu lại hội thoại vào file JSON (có thời gian). */
    public static void saveConversationHistory(String userInput, String botResponse) {
        List<Map<String, Object>> history = loadConversationHistory();

        String now = LocalDateTime.now().format(TIME_FORMAT);

        history.add(Map.of("role", "user", "content", userInput, "timestamp", now));
        history.add(Map.of("role", "assistant", "content", botResponse, "timestamp", now));

        try (Writer writer = new FileWriter(HISTORY_FILE)) {
            gson.toJson(history, writer);
        } catch (IOException e) {
            System.err.println("⚠️ Không thể lưu lịch sử: " + e.getMessage());
        }
    }

    /** 📖 Đọc lại lịch sử từ file conversation.json (nếu có). */
    private static List<Map<String, Object>> loadConversationHistory() {
        try (Reader reader = new FileReader(HISTORY_FILE)) {
            return gson.fromJson(reader, List.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /** 🧹 Xóa toàn bộ lịch sử hội thoại. */
    private static void clearConversationHistory() {
        try (Writer writer = new FileWriter(HISTORY_FILE)) {
            writer.write("[]");
        } catch (IOException e) {
            System.err.println("⚠️ Không thể xóa lịch sử: " + e.getMessage());
        }
    }
}
