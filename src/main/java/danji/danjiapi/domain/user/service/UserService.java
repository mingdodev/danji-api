package danji.danjiapi.domain.user.service;

import danji.danjiapi.domain.market.entity.Market;
import danji.danjiapi.domain.market.repository.MarketRepository;
import danji.danjiapi.domain.user.dto.request.UserCreateCustomerRequest;
import danji.danjiapi.domain.user.dto.request.UserCreateMerchantRequest;
import danji.danjiapi.domain.user.dto.response.UserCreateMerchantResponse;
import danji.danjiapi.domain.user.dto.response.UserCreateCustomerResponse;
import danji.danjiapi.domain.user.dto.response.UserMerchantMarketResponse;
import danji.danjiapi.domain.user.entity.User;
import danji.danjiapi.domain.user.repository.UserRepository;
import danji.danjiapi.global.exception.CustomException;
import danji.danjiapi.global.exception.ErrorMessage;
import danji.danjiapi.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final MarketRepository marketRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;

    public UserCreateCustomerResponse signupCustomer(UserCreateCustomerRequest request) {
        validateEmail(request.email());
        String encodedPassword = passwordEncoder.encode(request.password());

        User user = userRepository.save(User.create(request.email(), encodedPassword, request.name(), "CUSTOMER"));

        return UserCreateCustomerResponse.from(user.getId(), user.getName(), user.getRole().name());
    }

    @Transactional
    public UserCreateMerchantResponse signupMerchant(UserCreateMerchantRequest request, MultipartFile image) {
        validateEmail(request.email());
        String encodedPassword = passwordEncoder.encode(request.password());

        String imageUrl = (image != null && !image.isEmpty())
                ? s3Uploader.upload(image, "market", request.email())
                : null;

        User user = userRepository.save(User.create(request.email(), encodedPassword, request.name(), "MERCHANT"));
        Market market = marketRepository.save(Market.create(
                request.marketName(),
                request.marketAddress(),
                imageUrl,
                user));

        return UserCreateMerchantResponse.from(user.getId(), user.getName(), user.getRole().name(), market.getId());
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorMessage.USER_DUPLICATED_EMAIL);
        }
    }

    public UserMerchantMarketResponse getMarket(Long userId) {
        Market market = marketRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorMessage.MARKET_NOT_FOUND));

        return UserMerchantMarketResponse.from(market.getName(), market.getId(), market.getAddress());
    }
}
