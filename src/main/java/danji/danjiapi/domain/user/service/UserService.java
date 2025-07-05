package danji.danjiapi.domain.user.service;

import danji.danjiapi.domain.market.entity.Market;
import danji.danjiapi.domain.market.repository.MarketRepository;
import danji.danjiapi.domain.user.dto.request.UserCreateCustomerRequest;
import danji.danjiapi.domain.user.dto.request.UserCreateMerchantRequest;
import danji.danjiapi.domain.user.dto.response.UserCreateMerchantResponse;
import danji.danjiapi.domain.user.dto.response.UserCreateCustomerResponse;
import danji.danjiapi.domain.user.entity.User;
import danji.danjiapi.domain.user.repository.UserRepository;
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
        // TO DO: 이메일 중복 체크 -> 두 화면 유지 시 API 따로 생성?
        String encodedPassword = passwordEncoder.encode(request.password());

        User user = userRepository.save(User.create(request.email(), encodedPassword, request.name(), "CUSTOMER"));

        return UserCreateCustomerResponse.from(user.getId(), user.getName(), user.getRole().name());
    }


    @Transactional
    public UserCreateMerchantResponse signupMerchant(UserCreateMerchantRequest request) {
        String encodedPassword = passwordEncoder.encode(request.password());

        User user = userRepository.save(User.create(request.email(), encodedPassword, request.name(), "MERCHANT"));
        Market market = marketRepository.save(Market.create(
                request.marketName(),
                request.marketAddress(),
                request.marketImageUrl(),
                user));

        return UserCreateMerchantResponse.from(user.getId(), user.getName(), user.getRole().name(), market.getId());
    }
}
