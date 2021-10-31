package com.example.gostocksum.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@Controller
public class SumController {


    @RequestMapping("sum")
    public ResponseEntity<String> sum() throws IOException {
        String accessToken = ResourceProxy.getAccessToken();
        double amount = ResourceProxy.getAmountOf(ResourceProxy.getPurchaseOrders(accessToken));

        return ResponseEntity.ok(String.valueOf(amount));
    }
}
