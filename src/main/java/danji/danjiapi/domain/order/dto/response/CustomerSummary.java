package danji.danjiapi.domain.order.dto.response;

import danji.danjiapi.domain.user.entity.User;

public record CustomerSummary(
        Long id,
        String name
) {
    public static CustomerSummary from(User user) {
        return new CustomerSummary(
                user.getId(),
                user.getName()
        );
    }
}
