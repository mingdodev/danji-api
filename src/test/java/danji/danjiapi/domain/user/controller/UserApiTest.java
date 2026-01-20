package danji.danjiapi.domain.user.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import danji.danjiapi.domain.user.dto.request.UserCreateCustomerRequest;
import danji.danjiapi.domain.user.dto.request.UserCreateMerchantRequest;
import danji.danjiapi.domain.user.dto.response.UserCreateCustomerResponse;
import danji.danjiapi.domain.user.dto.response.UserCreateMerchantResponse;
import danji.danjiapi.domain.user.entity.Role;
import danji.danjiapi.domain.user.entity.User;
import danji.danjiapi.domain.user.repository.UserRepository;
import danji.danjiapi.global.response.ApiResponse;
import jakarta.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor
class UserApiTest {
    final MockMvcTester mvcTester;
    final ObjectMapper objectMapper;
    final UserRepository userRepository;

    @Test
    void signupCustomer() throws JsonProcessingException, UnsupportedEncodingException {
        var request = new UserCreateCustomerRequest("email@example.com", "1234", "tester");
        String requestJson = objectMapper.writeValueAsString(request);

        MvcTestResult result = mvcTester.post().uri("/api/users/signup/customer").contentType(MediaType.APPLICATION_JSON)
                .content(requestJson).exchange();

        assertThat(result)
                .hasStatusOk();

        String responseJson = result.getResponse().getContentAsString();

        var apiResponse = objectMapper.readValue(responseJson,
                new TypeReference<ApiResponse<UserCreateCustomerResponse>>() {});

        UserCreateCustomerResponse response = apiResponse.data();
        assertNotNull(response);
        assertThat(response.id()).isNotNull();

        User user = userRepository.findById(response.id()).orElseThrow();
        assertThat(user.getEmail()).isEqualTo(request.email());
        assertThat(user.getRole()).isEqualTo(Role.CUSTOMER);
    }

    @Test
    void signupMerchant() throws JsonProcessingException, UnsupportedEncodingException {
        var request = new UserCreateMerchantRequest("email@example.com", "1234", "tester", "test market", "test address 111");
        String requestJson = objectMapper.writeValueAsString(request);

        MockMultipartFile jsonPart = new MockMultipartFile("request", "", "application/json", requestJson.getBytes());

        MvcTestResult result = mvcTester.perform(multipart("/api/users/signup/merchant")
                        .file(jsonPart));

        assertThat(result)
                .hasStatusOk();

        String responseJson = result.getResponse().getContentAsString();

        var apiResponse = objectMapper.readValue(responseJson,
                new TypeReference<ApiResponse<UserCreateMerchantResponse>>() {});

        UserCreateMerchantResponse response = apiResponse.data();
        assertNotNull(response);
        assertThat(response.id()).isNotNull();
        assertThat(response.marketId()).isNotNull();

        User user = userRepository.findById(response.id()).orElseThrow();
        assertThat(user.getEmail()).isEqualTo(request.email());
        assertThat(user.getRole()).isEqualTo(Role.MERCHANT);
    }
}