package com.henrique.votacao.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class LauncherUI {

    private static final String SWAGGER_URL = "http://localhost:8080/swagger-ui/index.html";
    private static final String H2_URL = "http://localhost:8080/h2-console";

    @EventListener(ApplicationReadyEvent.class)
    public void openSwaggerAndH2() {
        try {
            System.out.println("Abrindo Swagger e H2 Console...");

            Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", SWAGGER_URL});
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", H2_URL});

            System.out.println("Swagger UI e H2 Console abertos automaticamente!");
        } catch (Exception e) {
            System.err.println("Erro ao abrir Swagger/H2: " + e.getMessage());
        }
    }
}
