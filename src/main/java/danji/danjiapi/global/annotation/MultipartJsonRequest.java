package danji.danjiapi.global.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.http.MediaType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(
                encoding = @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)
        )
)
public @interface MultipartJsonRequest {
}
