import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CurrencyConverter {

    private static final String BASE_API_URL = "https://v6.exchangerate-api.com/v6/";
    private static final String API_KEY = "a3b64611ddf1cbfe0c0bf21c";

    private static final List<String> CURRENCY_CODES = Arrays.asList("ARS", "BOB", "BRL", "CLP", "COP", "USD");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Bem-vindo ao Conversor de Moedas!");

        while (true) {
            System.out.println("\nSelecione uma opção:");
            System.out.println("1 - Conversão de Moeda");
            System.out.println("2 - Sair");

            int option = scanner.nextInt();
            scanner.nextLine(); // Consumir a nova linha após o nextInt()

            switch (option) {
                case 1:
                    performCurrencyConversion(scanner);
                    break;
                case 2:
                    System.out.println("Saindo... Obrigado por usar o Conversor de Moedas!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        }
    }

    private static void performCurrencyConversion(Scanner scanner) {
        System.out.println("Moedas disponíveis para conversão: " + CURRENCY_CODES);
        System.out.println("Digite a moeda de origem:");
        String sourceCurrency = scanner.nextLine().toUpperCase();

        if (CURRENCY_CODES.contains(sourceCurrency)) {
            System.out.println("Digite a moeda de destino:");
            String targetCurrency = scanner.nextLine().toUpperCase();

            if (CURRENCY_CODES.contains(targetCurrency)) {
                System.out.println("Digite o valor a ser convertido:");
                double amount = scanner.nextDouble();

                double exchangeRate = getExchangeRate(sourceCurrency, targetCurrency);
                if (exchangeRate != -1) {
                    double convertedAmount = amount * exchangeRate;
                    System.out.printf("%.2f %s = %.2f %s%n", amount, sourceCurrency, convertedAmount, targetCurrency);
                } else {
                    System.out.println("Erro ao obter as taxas de câmbio.");
                }
            } else {
                System.out.println("Moeda de destino selecionada não está disponível para conversão.");
            }
        } else {
            System.out.println("Moeda de origem selecionada não está disponível para conversão.");
        }
    }

    private static double getExchangeRate(String sourceCurrency, String targetCurrency) {
        String apiUrl = buildApiUrl(sourceCurrency);

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                Gson gson = new Gson();
                ExchangeRateResponse exchangeRateResponse = gson.fromJson(responseBody, ExchangeRateResponse.class);

                JsonObject rates = exchangeRateResponse.conversion_rates;
                if (rates.has(targetCurrency)) {
                    return rates.get(targetCurrency).getAsDouble();
                } else {
                    System.out.println("Moeda de destino não encontrada.");
                    return -1;
                }
            } else {
                System.out.println("Erro ao obter as taxas de câmbio: " + response.statusCode());
                return -1;
            }
        } catch (Exception e) {
            System.out.println("Erro ao fazer a requisição: " + e.getMessage());
            return -1;
        }
    }

    private static String buildApiUrl(String sourceCurrency) {
        return BASE_API_URL + API_KEY + "/latest/" + sourceCurrency;
    }

    static class ExchangeRateResponse {
        public JsonObject conversion_rates;
    }
}
