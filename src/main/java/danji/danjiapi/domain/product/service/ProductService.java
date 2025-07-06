package danji.danjiapi.domain.product.service;

import danji.danjiapi.domain.market.entity.Market;
import danji.danjiapi.domain.market.repository.MarketRepository;
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
    private final MarketRepository marketRepository;
    private final CurrentUserResolver currentUserResolver;

    public ProductCreateResponse add(ProductCreateRequest request) {
        Long currentUserId = currentUserResolver.getCurrentUserId();

        Market market = marketRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new CustomException(ErrorMessage.MARKET_NOT_FOUND));

        Product product = productRepository.save(Product.create(request.name(), request.price(), request.minQuantity(), request.maxQuantity(), market));

        return ProductCreateResponse.from(product);
    }

    public void delete(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorMessage.PRODUCT_NOT_FOUND));

        AccessValidator.validateProductAccess(product, currentUserResolver.getCurrentUserId());

        productRepository.deleteById(productId);
    }
}
