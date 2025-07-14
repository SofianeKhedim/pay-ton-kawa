package com.example.mspr4.Services;
import com.example.mspr4.Config.RabbitConfig;
import com.example.mspr4.Entities.Product;
import com.example.mspr4.Repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductRepository productRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    @RabbitListener(queues = RabbitConfig.ORDER_QUEUE)
    @Transactional
    public void handleOrderEvent(String messageJson) {
        try {
            Map<String, Object> message = mapper.readValue(messageJson, Map.class);
            Map<String, Object> data = (Map<String, Object>) message.get("data");

            String orderId = (String) data.get("orderId");
            String clientId = (String) data.get("clientId");
            List<Map<String, Object>> products = (List<Map<String, Object>>) data.get("products");

            System.out.println("📦 Traitement de la commande " + orderId);

            boolean stockOk = checkAndReserve(products);

            Map<String, Object> response = new HashMap<>();
            response.put("event", stockOk ? "stock_validated" : "stock_failed");
            response.put("data", Map.of(
                    "orderId", orderId,
                    "clientId", clientId
            ));

            rabbitTemplate.convertAndSend(RabbitConfig.STOCK_QUEUE, mapper.writeValueAsString(response));
            System.out.println("✅ Événement " + response.get("event") + " envoyé pour la commande " + orderId);

        } catch (Exception e) {
            System.err.println("❌ Erreur traitement message : " + e.getMessage());
        }
    }

    private boolean checkAndReserve(List<Map<String, Object>> products) {
        for (Map<String, Object> p : products) {
            int id = Integer.parseInt(p.get("productId").toString());
            int qty = Integer.parseInt(p.get("quantity").toString());

            Optional<Product> opt = productRepository.findById(id);
            if (opt.isEmpty() || opt.get().getQuantity() < qty) {
                System.out.println("❌ Stock insuffisant pour le produit " + id);
                return false;
            }
        }

        // Si tout est OK : décrémente le stock
        for (Map<String, Object> p : products) {
            int id = Integer.parseInt(p.get("productId").toString());
            int qty = Integer.parseInt(p.get("quantity").toString());

            Product prod = productRepository.findById(id).orElseThrow();
            prod.setQuantity(prod.getQuantity() - qty);
            productRepository.save(prod);
        }

        return true;
    }
}