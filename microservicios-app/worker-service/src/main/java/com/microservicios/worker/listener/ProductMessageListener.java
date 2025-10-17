package com.microservicios.worker.listener;

import com.microservicios.worker.dto.ProductMessage;
import com.microservicios.worker.service.ProductSyncService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductMessageListener {
    

    @Autowired
    private ProductSyncService productSyncService;
    
    @JmsListener(destination = "${jms.queue.product}")
    public void receiveMessage(ProductMessage message) {
        log.info("Mensaje recibido de la cola: {}", message);
        
        try {
            productSyncService.syncProduct(message);
            log.info("Mensaje procesado exitosamente");
        } catch (Exception e) {
            log.error("Error al procesar mensaje", e);
        }
    }
}
