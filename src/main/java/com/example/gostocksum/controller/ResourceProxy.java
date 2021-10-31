package com.example.gostocksum.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public class ResourceProxy {

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static double getAmountOf(JsonNode jsonNode) {
        List<JsonNode> prices = jsonNode.findValues("total_price_gross_money");
        return prices.stream()
            .map(node -> Double.parseDouble(node.get("amount").asText()))
            .reduce(0.0, Double::sum);
    }

    public static JsonNode getPurchaseOrders(String accessToken) throws IOException {
        Request request = new Request.Builder()
            .url("https://resource_server.pl/api/249/purchase_orders")
            .addHeader("Authorization", "Bearer " + accessToken)
            .build();

        Response response = client.newCall(request).execute();
        return responseToJsonNode(response);
    }

    public static String getAccessToken() {
        RequestBody requestBody = new FormBody.Builder()
            .add("grant_type", "password")
            .add("username", "zadanie@zadanie.com")
            .add("password", "zadanie")
            .add("client_id", "client-id")
            .add("client_secret", "client-secret")
            .build();
        Request request = new Request.Builder()
            .url("https://authorization_server.pl/oauth/token")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .post(requestBody)
            .build();

        try {
            Response response = client.newCall(request).execute();
            return responseToJsonNode(response).get("access_token").asText();
        }
        catch (IOException e) {
            System.out.println(e);
            return "Failed during operating on request and response.";
        }
    }

    private static JsonNode responseToJsonNode(Response response) throws IOException {
        return objectMapper.readTree(response.body().string());
    }
}
