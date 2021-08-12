package com.github.olaleyeone.webkey.util;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

public class ResponseHandler {

    private final Gson gson;

    public ResponseHandler(Gson gson) {
        this.gson = gson;
    }

    public void sendData(HttpExchange exchange, Object data) throws IOException {
        try {
            String response = gson.toJson(data);
            exchange.getResponseHeaders().add("content-type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
                os.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, e);
        }
    }

    public void sendError(HttpExchange exchange, Exception e) throws IOException {
        Map<String, Object> map = Collections.singletonMap("message", e.getMessage());
        String response = gson.toJson(map);
        exchange.getResponseHeaders().add("content-type", "application/json");
        exchange.sendResponseHeaders(500, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
            os.flush();
        }
    }
}
