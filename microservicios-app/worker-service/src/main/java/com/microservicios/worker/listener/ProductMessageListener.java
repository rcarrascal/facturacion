package com.microservicios.worker.listener;

import com.microservicios.worker.dto.ProductMessage;
import com.microservicios.worker.service.ProductSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ProductMessageListener {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductMessageListener.class);
    
    @Autowired
    private ProductSyncService productSyncService;
    
    @JmsListener(destination = "${jms.queue.product}")
    public void receiveMessage(ProductMessage message) {
        logger.info("Mensaje recibido de la cola: {}", message);
        
        try {
            productSyncService.syncProduct(message);
            logger.info("Mensaje procesado exitosamente");
        } catch (Exception e) {
            logger.error("Error al procesar mensaje", e);
            // En un escenario real, aquí podrías implementar reintentos o DLQ
        }
    }
}
