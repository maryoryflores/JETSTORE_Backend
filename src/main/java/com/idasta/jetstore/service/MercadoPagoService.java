package com.idasta.jetstore.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MercadoPagoService {

    private final RestClient restClient;
    private final String accessToken;

    public MercadoPagoService(@Value("${mercadopago.access-token:}") String accessToken) {
        this.accessToken = accessToken == null ? "" : accessToken.trim();
        this.restClient = RestClient.builder()
                .baseUrl("https://api.mercadopago.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> crearPreferencia(Map<String, Object> preferenceData) {
        if (accessToken.isBlank()) {
            // Mock mode: sin credenciales, devuelve init_point fake para desarrollo
            Map<String, Object> mock = new HashMap<>();
            String fakeId = "MOCK-" + UUID.randomUUID().toString().substring(0, 8);
            mock.put("id", fakeId);
            mock.put("init_point", "https://www.mercadopago.com.pe/checkout/v1/redirect?pref_id=" + fakeId);
            mock.put("sandbox_init_point", "https://sandbox.mercadopago.com.pe/checkout/v1/redirect?pref_id=" + fakeId);
            System.out.println("[MercadoPago MOCK] Preferencia creada sin ACCESS_TOKEN. Configure MERCADOPAGO_ACCESS_TOKEN para modo real.");
            System.out.println("[MercadoPago MOCK] Data: " + preferenceData);
            return mock;
        }

        try {
            return restClient.post()
                    .uri("/checkout/preferences")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .body(preferenceData)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear preferencia en Mercado Pago: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> obtenerPago(String paymentId) {
        if (accessToken.isBlank() || paymentId == null || paymentId.startsWith("MOCK")) {
            // Mock: simular pago aprobado
            Map<String, Object> mock = new HashMap<>();
            mock.put("id", paymentId);
            mock.put("status", "approved");
            return mock;
        }
        try {
            return restClient.get()
                    .uri("/v1/payments/{id}", paymentId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            System.out.println("[MercadoPago] No se pudo verificar pago " + paymentId + ": " + e.getMessage());
            return null;
        }
    }
}
