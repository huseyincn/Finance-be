package com.huseyincan.financeportfolio.service.user;

import com.huseyincan.financeportfolio.repository.UserRepository;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ImageService {
    private UserRepository userRepository;

    @Autowired
    public ImageService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private final String IMAGE_DIRECTORY = "src/main/resources/static/images";
    public String saveImageToStorage(MultipartFile imageFile) throws IOException {
        String uniqueFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();

        Path uploadPath = Path.of(IMAGE_DIRECTORY);
        Path filePath = uploadPath.resolve(uniqueFileName);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Resize the image
        BufferedImage originalImage = ImageIO.read(imageFile.getInputStream());
        BufferedImage resizedImage = Thumbnails.of(originalImage)
                .size(400, 400)  // Change this to your desired size
                .asBufferedImage();

        // Save the resized image
        ImageIO.write(resizedImage, "jpg", filePath.toFile());  // Change "jpg" to your desired format

        return uniqueFileName;
    }

    // To view an image
    public byte[] getImage(String imageName) throws IOException {
        Path imagePath = Path.of(IMAGE_DIRECTORY, imageName);
        if (Files.exists(imagePath)) {
            return Files.readAllBytes(imagePath);
        } else {
            return null; // Handle missing images
        }
    }

    // Delete an image
    public String deleteImage(String imageName) throws IOException {
        Path imagePath = Path.of(IMAGE_DIRECTORY, imageName);
        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
            return "Success";
        } else {
            return "Failed"; // Handle missing images
        }
    }
}
