package com.microservicios.worker.repository;

import com.microservicios.worker.model.ProductMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProductMongoRepository extends MongoRepository<ProductMongo, String> {
    Optional<ProductMongo> findByProductId(Long productId);
}
