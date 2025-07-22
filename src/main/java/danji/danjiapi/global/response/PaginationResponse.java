package danji.danjiapi.global.response;

import java.util.List;

public record PaginationResponse<T>(
        List<T> items,
        boolean hasNext
) {
}
