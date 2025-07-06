package danji.danjiapi.domain.product.service;

import danji.danjiapi.domain.product.dto.request.ProductCreateRequest;
import danji.danjiapi.domain.product.dto.response.ProductCreateResponse;
import danji.danjiapi.domain.product.entity.Product;
import danji.danjiapi.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductCreateResponse add(ProductCreateRequest request) {
        Product product = productRepository.save(Product.create(request.name(), request.price(), request.minQuantity(), request.maxQuantity()));

        return ProductCreateResponse.from(product);
    }
}
