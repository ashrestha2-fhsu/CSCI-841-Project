package CSCI_841_Project.backend.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}") // ✅ Read upload directory from application.properties
    private String uploadDir;

    /**
     * Stores the uploaded file and returns its URL.
     *
     * @param file The uploaded file
     * @return The URL of the stored file
     * @throws IOException If file saving fails
     */
    public String storeFile(MultipartFile file) throws IOException {
        // ✅ Generate a unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // ✅ Ensure the upload directory exists
        Path uploadPath = Path.of(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // ✅ Save the file to the server
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // ✅ Return file URL (Modify based on how files are served)
        return uniqueFilename;
//        return "/uploads/" + uniqueFilename;
    }

    public Resource loadFile(String filename) throws IOException {
        Path filePath = Path.of(uploadDir).resolve(filename);
        return new UrlResource(filePath.toUri());
    }
}