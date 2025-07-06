package danji.danjiapi.domain.product.service;

import danji.danjiapi.domain.product.dto.request.ProductCreateRequest;
import danji.danjiapi.domain.product.dto.response.ProductCreateResponse;
import danji.danjiapi.domain.product.entity.Product;
import danji.danjiapi.domain.product.repository.ProductRepository;
import danji.danjiapi.global.exception.CustomException;
import danji.danjiapi.global.exception.ErrorMessage;
import danji.danjiapi.global.util.resolver.CurrentUserResolver;
import danji.danjiapi.global.util.validator.AccessValidator;
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

    public Void delete(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorMessage.PRODUCT_NOT_FOUND));

        AccessValidator.validateProductAccess(product, CurrentUserResolver.getCurrentUserId());

        productRepository.deleteById(productId);

        return null;
    }
}
