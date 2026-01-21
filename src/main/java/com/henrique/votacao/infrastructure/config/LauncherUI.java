package com.henrique.votacao.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "launcher.ui.disabled", havingValue = "false")
public class LauncherUI {

    private static final String SWAGGER_URL = "http://localhost:8080/swagger-ui/index.html";
    private static final String H2_URL = "http://localhost:8080/h2-console";

    @Value("${launcher.ui.disabled:true}")
    private boolean launcherDesativado;

    @EventListener(ApplicationReadyEvent.class)
    public void openSwaggerAndH2() {
        if (launcherDesativado) {
            return;
        }

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
