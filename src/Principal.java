import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Principal {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    public static void main(String[] args) {
        String apiKey = "a3b64611ddf1cbfe0c0bf21c";  // Substitua "SUA_CHAVE_DA_API" pela sua chave de API

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://api.exchangerate-api.com/v4/latest/USD"))
                .header("Authorization", "Bearer " + apiKey)
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Imprimir a resposta
            System.out.println(response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

