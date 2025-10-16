package com.microservicios.worker.service;

import com.microservicios.worker.dto.ProductMessage;
import com.microservicios.worker.model.ProductMongo;
import com.microservicios.worker.repository.ProductMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class ProductSyncService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductSyncService.class);
    
    @Autowired
    private ProductMongoRepository productMongoRepository;
    
    public void syncProduct(ProductMessage message) {
        try {
            logger.info("Sincronizando producto con ID: {} en MongoDB", message.getId());
            
            ProductMongo productMongo = new ProductMongo();
            productMongo.setProductId(message.getId());
            productMongo.setName(message.getName());
            productMongo.setDescription(message.getDescription());
            productMongo.setPrice(message.getPrice());
            productMongo.setStock(message.getStock());
            productMongo.setCategory(message.getCategory());
            productMongo.setCreatedAt(message.getCreatedAt());
            productMongo.setUpdatedAt(message.getUpdatedAt());
            productMongo.setSyncedAt(LocalDateTime.now());
            
            productMongoRepository.save(productMongo);
            
            logger.info("Producto con ID: {} sincronizado exitosamente en MongoDB", message.getId());
        } catch (Exception e) {
            logger.error("Error al sincronizar producto con ID: {} en MongoDB", message.getId(), e);
            throw new RuntimeException("Error al sincronizar producto en MongoDB", e);
        }
    }
}
