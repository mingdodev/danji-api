package danji.danjiapi.global.response;

import java.util.List;
import org.springframework.data.domain.Slice;

public record PaginationResponse<T>(
        List<T> items,
        boolean hasNext
) {
    public static <T> PaginationResponse<T> from(List<T> items, boolean hasNext) {
        return new PaginationResponse<>(items, hasNext);
    }
    public static <T> PaginationResponse<T> from(Slice<T> slice) {
        return new PaginationResponse<>(slice.getContent(), slice.hasNext());
    }
}
