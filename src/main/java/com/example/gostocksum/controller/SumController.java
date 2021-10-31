package com.example.gostocksum.controller;

import com.example.gostocksum.ResourceServerProxy;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@Controller
public class SumController {

    private final ResourceServerProxy proxy;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SumController(ResourceServerProxy proxy) {
        this.proxy = proxy;
    }

    @RequestMapping("sum")
    public ResponseEntity<String> sum() {
        return ResponseEntity.ok(proxy.callData());
    }

    private JsonNode responseToJsonNode(Response response) throws IOException {
        return objectMapper.readTree(response.body().string());
    }

    private double getAmountOf(JsonNode jsonNode) {
        List<JsonNode> prices = jsonNode.findValues("total_price_gross_money");
        return prices.stream()
            .map(node -> Double.parseDouble(node.get("amount").asText()))
            .reduce(0.0, Double::sum);
    }
}
