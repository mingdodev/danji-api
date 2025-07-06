package danji.danjiapi.global.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import danji.danjiapi.global.exception.CustomException;
import danji.danjiapi.global.exception.ErrorMessage;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.cloudfront.domain}")
    private String cloudFrontDomain;

    public String upload(MultipartFile file, String dirName, String userEmail) {
        String filename = getFilenameFromEmail(file, dirName, userEmail);

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3.putObject(new PutObjectRequest(bucket, filename, file.getInputStream(), metadata));

            return cloudFrontDomain + "/" + filename;

        } catch (IOException e) {
            throw new CustomException(ErrorMessage.USER_IMAGE_UPLOAD_FAILED);
        }
    }

    private static String getFilenameFromEmail(MultipartFile file, String dirName, String userEmail) {
        String emailPrefix = userEmail.substring(0, userEmail.indexOf('@'));

        String extension = Optional.ofNullable(file.getOriginalFilename())
                .filter(name -> name.contains("."))
                .map(name -> name.substring(file.getOriginalFilename().lastIndexOf(".")))
                .orElse("");

        String filename = String.format("%s/%s%s", dirName, emailPrefix, extension);
        return filename;
    }
}