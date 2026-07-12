package duanspringboot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URI;
import java.text.Normalizer;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.cv-bucket}")
    private String bucketName;

    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vui lòng chọn file"));
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = getExtension(originalFilename);
            String safeFileName = sanitizeFileName(originalFilename);

            String key = "cv/" + UUID.randomUUID() + "-" + safeFileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            String fileUrl = "/api/files/view?key=" + key;

            return ResponseEntity.ok(Map.of(
                    "url", fileUrl,
                    "s3Key", key,
                    "fileName", originalFilename != null ? originalFilename : safeFileName,
                    "fileType", extension.replace(".", "").toUpperCase(),
                    "fileSize", file.getSize()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Lỗi tải file lên S3"));
        }
    }

    @GetMapping("/view")
    public ResponseEntity<?> viewFile(@RequestParam("key") String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(getObjectRequest)
                    .build();

            URI presignedUrl = s3Presigner.presignGetObject(presignRequest).url().toURI();

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(presignedUrl)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Không thể mở CV"));
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ".pdf";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "cv.pdf";
        }

        String normalized = Normalizer.normalize(fileName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return normalized
                .replaceAll("[^a-zA-Z0-9\\.\\-_]", "_")
                .replaceAll("_+", "_");
    }
}
