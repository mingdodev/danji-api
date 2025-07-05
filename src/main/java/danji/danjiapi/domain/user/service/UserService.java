package danji.danjiapi.domain.user.service;

import danji.danjiapi.domain.market.entity.Market;
import danji.danjiapi.domain.market.repository.MarketRepository;
import danji.danjiapi.domain.user.dto.request.UserCreateCustomerRequest;
import danji.danjiapi.domain.user.dto.request.UserCreateMerchantRequest;
import danji.danjiapi.domain.user.dto.response.UserCreateMerchantResponse;
import danji.danjiapi.domain.user.dto.response.UserCreateCustomerResponse;
import danji.danjiapi.domain.user.entity.User;
import danji.danjiapi.domain.user.repository.UserRepository;
import danji.danjiapi.global.exception.CustomException;
import danji.danjiapi.global.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final MarketRepository marketRepository;
    private final PasswordEncoder passwordEncoder;

    public UserCreateCustomerResponse signupCustomer(UserCreateCustomerRequest request) {
        validateEmail(request.email());
        String encodedPassword = passwordEncoder.encode(request.password());

        User user = userRepository.save(User.create(request.email(), encodedPassword, request.name(), "CUSTOMER"));

        return UserCreateCustomerResponse.from(user.getId(), user.getName(), user.getRole().name());
    }

    @Transactional
    public UserCreateMerchantResponse signupMerchant(UserCreateMerchantRequest request) {
        validateEmail(request.email());
        String encodedPassword = passwordEncoder.encode(request.password());

        User user = userRepository.save(User.create(request.email(), encodedPassword, request.name(), "MERCHANT"));
        Market market = marketRepository.save(Market.create(
                request.marketName(),
                request.marketAddress(),
                request.marketImageUrl(),
                user));

        return UserCreateMerchantResponse.from(user.getId(), user.getName(), user.getRole().name(), market.getId());
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorMessage.USER_DUPLICATED_EMAIL);
        }
    }
}
