package com.huseyincan.financeportfolio.service.user;

import com.huseyincan.financeportfolio.dao.User;
import com.huseyincan.financeportfolio.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class UserInfoService {

    private UserRepository userRepository;
    private ImageService imageService;

    @Autowired
    public UserInfoService(UserRepository userRepository, ImageService imageService) {
        this.userRepository = userRepository;
        this.imageService = imageService;
    }

    public User fetchUserData(String email) {
        User a = userRepository.findItemByEmail(email);
        return new User(a.getId(), a.getEmail(), a.getPhoto());
    }

    public boolean saveUserImageToMongo(String email, MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        BufferedImage resizedImage = Thumbnails.of(originalImage)
                .size(400, 400)  // Change this to your desired size
                .asBufferedImage();
        // Convert BufferedImage to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);
        byte[] imageInByte = baos.toByteArray();
        User user = userRepository.findItemByEmail(email);
        user.setPhoto(imageInByte);
        userRepository.save(user);
        return true;
    }
}
