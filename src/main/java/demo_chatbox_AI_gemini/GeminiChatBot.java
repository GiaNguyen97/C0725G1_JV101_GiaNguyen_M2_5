package demo_chatbox_AI_gemini;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.Scanner;

import com.google.gson.*;

public class GeminiChatBot {

    private static final String MODEL = ConfigReader.get("MODEL");
    private static final String API_KEY = ConfigReader.get("API_KEY");
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1/models/"
            + MODEL + ":generateContent?key=" + API_KEY;


    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        HttpClient client = HttpClient.newHttpClient();

        System.out.println("🤖 Gemini Chatbot đã sẵn sàng!");
        System.out.println("Gõ 'exit' để thoát.\n");

        while (true) {
            System.out.print("👤 Bạn: ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) break;
            System.out.println("... ⏳ Đã gửi, đang chờ Gemini phản hồi...");
            // Xử lý các ký tự đặc biệt trong input để đảm bảo JSON hợp lệ
            String sanitizedInput = input.replace("\\", "\\\\").replace("\"", "\\\"");

            // JSON body đúng chuẩn cho Gemini
            String requestBody = """
                    {
                      "contents": [
                        {
                          "parts": [
                            {"text": "%s"}
                          ]
                        }
                      ]
                    }
                    """.formatted(sanitizedInput); // Đã thay thế .formatted(input.replace("\"", "\\\""))

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());



            try {
                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

                if (json.has("candidates")) {
                    // Cải tiến việc phân tích JSON để dễ đọc hơn và an toàn hơn
                    JsonElement textElement = json
                            .getAsJsonArray("candidates").get(0).getAsJsonObject()
                            .getAsJsonObject("content")
                            .getAsJsonArray("parts").get(0).getAsJsonObject()
                            .get("text");

                    if (textElement != null && textElement.isJsonPrimitive()) {
                        String answer = textElement.getAsString();
                        System.out.println("🤖 Gemini: " + answer + "\n");
                    } else {
                        System.out.println("🤖 Gemini: ❌ Lỗi: Không tìm thấy phần 'text' trong phản hồi.\n");
                    }

                } else if (json.has("error")) {
                    String errorMessage = json.getAsJsonObject("error").get("message").getAsString();
                    System.out.println("🤖 Gemini: ❌ Lỗi API: " + errorMessage + "\n");
                } else {
                    System.out.println("🤖 Gemini: ❌ Lỗi phản hồi từ API (Không có 'candidates' hay 'error').\n");
                }
            } catch (JsonParseException | IllegalStateException e) {
                System.out.println("🤖 Gemini: ❌ Lỗi phân tích JSON: " + e.getMessage() + "\n");
            }
        }

        scanner.close();
    }
}