package com.roman.sapun.java.socialmedia.service.implementation;

import com.roman.sapun.java.socialmedia.dto.FileDTO;
import com.roman.sapun.java.socialmedia.dto.image.ResponseImageDTO;
import com.roman.sapun.java.socialmedia.entity.ImageEntity;
import com.roman.sapun.java.socialmedia.entity.PostEntity;
import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.exception.InvalidImageNumberException;
import com.roman.sapun.java.socialmedia.exception.PostNotFoundException;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import com.roman.sapun.java.socialmedia.repository.ImageRepository;
import com.roman.sapun.java.socialmedia.repository.PostRepository;
import com.roman.sapun.java.socialmedia.repository.UserRepository;
import com.roman.sapun.java.socialmedia.service.ImageService;
import com.roman.sapun.java.socialmedia.service.UserService;
import com.roman.sapun.java.socialmedia.util.IdentifierGenerator;
import com.roman.sapun.java.socialmedia.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    private final ImageUtil imageUtil;
    private final IdentifierGenerator identifierGenerator;
    private final UserRepository userRepository;
    private static final String USER_NOT_FOUND_EXCEPTION_RESPONSE = "User not found during processing images";

    @Lazy
    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository, PostRepository postRepository, UserService userService,
                            ImageUtil imageUtil, IdentifierGenerator identifierGenerator,
                            UserRepository userRepository) {
        this.imageRepository = imageRepository;
        this.postRepository = postRepository;
        this.userService = userService;
        this.imageUtil = imageUtil;
        this.userRepository = userRepository;
        this.identifierGenerator = identifierGenerator;
    }

    @Override
    public ResponseImageDTO uploadImageForUser(MultipartFile image, String username) throws IOException, UserNotFoundException {
        var imageEntity = new ImageEntity();
        var user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        imageEntity.setUser(user);
        return uploadImage(image, imageEntity);
    }

    @Override
    public List<ResponseImageDTO> uploadImagesForPost(List<MultipartFile> images, String postId, Authentication authentication) throws InvalidImageNumberException {
        if(images.size() >= 7) throw new InvalidImageNumberException("Image number cannot be more than 6");
        return images.stream()
                .map(file -> uploadImageForPost(file, postId, authentication))
                .collect(Collectors.toList());
    }

    private ResponseImageDTO uploadImageForPost(MultipartFile image, String postId, Authentication authentication) {
        try {
            var post = postRepository.findByIdentifier(postId).orElseThrow(PostNotFoundException::new);
            var user = userService.findUserByAuth(authentication);
            if (post.getAuthor() != user) {
                throw new PostNotFoundException();
            }
            var imageEntity = new ImageEntity();
            imageEntity.setPost(post);
            return uploadImage(image, imageEntity);
        } catch (IOException | PostNotFoundException e) {
            throw new RuntimeException(USER_NOT_FOUND_EXCEPTION_RESPONSE + " for post" + e.getMessage());
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseImageDTO uploadImage(MultipartFile image, ImageEntity imageEntity) throws IOException {
        var imageData = imageUtil.compressImage(image.getBytes());
        imageEntity.setImageData(imageData);
        imageEntity.setName(image.getOriginalFilename());
        imageEntity.setType(image.getContentType());
        imageEntity.setIdentifier(identifierGenerator.generateUniqueIdentifier());
        var savedImage = imageRepository.save(imageEntity);
        return new ResponseImageDTO(imageEntity.getIdentifier(),
                new FileDTO(savedImage, imageUtil.decompressImage(savedImage.getImageData())));
    }

    @Override
    public List<ResponseImageDTO> getImagesByPost(String postId) throws PostNotFoundException {
        var post = postRepository.getPostEntityByIdentifier(postId).orElseThrow(PostNotFoundException::new);
        return getImagesByPost(post);
    }

    @Override
    public List<ResponseImageDTO> getImagesByPost(PostEntity post) {
        return post.getImages().stream().parallel()
                .map(imageEntity -> new ResponseImageDTO(imageEntity.getIdentifier(), new FileDTO
                        (imageEntity, imageUtil.decompressImage(imageEntity.getImageData()))))
                .toList();
    }

    @Override
    public FileDTO getImageByUser(Authentication authentication) throws UserNotFoundException {
        var user = userService.findUserByAuth(authentication);
        var image = imageRepository.findByUser(user);
        return new FileDTO(image, imageUtil.decompressImage(image.getImageData()));
    }

    @Override
    public Map<String, FileDTO> getImagesByUsers(Set<UserEntity> userEntities)  {
        var userImages = new HashMap<String, FileDTO>();
        for (UserEntity userEntity : userEntities) {
                FileDTO userImage = getImageByUser(userEntity.getUsername());
                userImages.put(userEntity.getUsername(), userImage);
        }
        return userImages;
    }

    @Override
    public FileDTO getImageByUser(String username)  {
        try {
            var user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
            var image = imageRepository.findByUser(user);
            return new FileDTO(image, imageUtil.decompressImage(image.getImageData()));
        } catch (UserNotFoundException e) {
            throw new RuntimeException(USER_NOT_FOUND_EXCEPTION_RESPONSE + " for User", e);
        }
    }
}
