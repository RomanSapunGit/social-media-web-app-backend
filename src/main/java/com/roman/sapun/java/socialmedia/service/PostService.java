package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.RequestPostDTO;
import com.roman.sapun.java.socialmedia.dto.PostDTO;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

public interface PostService {
    PostDTO createPost(RequestPostDTO requestPostDTO, Authentication authentication);

    Map<String, Object> getPosts(int pageNumber);

    Map<String, Object> findPostsByTitleContaining(String title, int pageNumber);

    Map<String, Object> findPostsByTags(List<String> tags, int pageNumber);
}
