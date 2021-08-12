package com.github.olaleyeone.webkey;

import com.github.olaleyeone.webkey.model.WebKey;
import com.github.olaleyeone.webkey.util.KeyParser;
import com.github.olaleyeone.webkey.util.ResponseHandler;
import com.github.olaleyeone.webkey.util.StreamReader;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Executors;

public class Application {
    static Gson gson = new Gson();
    static Base64.Decoder decoder = Base64.getDecoder();

    public static void main(String[] args) throws IOException {
        int port = Optional.ofNullable(System.getenv("SERVER_PORT"))
                .map(Integer::valueOf)
                .orElse(8080);
        String hostname = Optional.ofNullable(System.getenv("SERVER_HOSTNAME"))
                .orElse("0.0.0.0");
        int threadPoolSize = Optional.ofNullable(System.getenv("SERVER_THREAD_POOL_SIZE"))
                .map(Integer::valueOf)
                .orElse(1);
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(hostname, port), 0);
        httpServer.setExecutor(Executors.newFixedThreadPool(threadPoolSize));
        ResponseHandler responseHandler = new ResponseHandler(gson);
        StreamReader streamReader = new StreamReader();
        KeyParser keyParser = new KeyParser();

        httpServer.createContext("/health", exchange -> responseHandler.sendData(exchange, Collections.singletonMap("status", "UP")));

        httpServer.createContext("/from-public-key", exchange -> {
            try {
                RSAPublicKey rsaPublicKey = keyParser.getRsaPublicKey(streamReader.getRequestData(exchange.getRequestBody()), "RSA");
                responseHandler.sendData(exchange, WebKey.fromRsaPublicKey(rsaPublicKey));
            } catch (Exception e) {
                e.printStackTrace();
                responseHandler.sendError(exchange, e);
            }
        });

        httpServer.createContext("/from-pem", exchange -> {
            try {
                byte[] data = streamReader.getRequestData(exchange.getRequestBody());
                String pemString = new String(data);
                RSAPublicKey rsaPublicKey = keyParser.getRsaPublicKey(decoder.decode(pemString), "RSA");
                responseHandler.sendData(exchange, WebKey.fromRsaPublicKey(rsaPublicKey));
            } catch (Exception e) {
                e.printStackTrace();
                responseHandler.sendError(exchange, e);
            }
        });

        httpServer.start();
        System.out.printf("Server listening on %s:%d\n", httpServer.getAddress().getAddress(), httpServer.getAddress().getPort());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            httpServer.stop(1);
            System.out.println("Server stopped");
        }));
    }
}
