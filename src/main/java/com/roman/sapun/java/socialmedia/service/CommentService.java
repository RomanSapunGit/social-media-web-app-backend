package com.roman.sapun.java.socialmedia.service;

import com.roman.sapun.java.socialmedia.dto.comment.CommentDTO;
import com.roman.sapun.java.socialmedia.dto.comment.RequestCommentDTO;
import com.roman.sapun.java.socialmedia.dto.comment.ResponseCommentDTO;
import com.roman.sapun.java.socialmedia.dto.page.CommentPageDTO;
import com.roman.sapun.java.socialmedia.exception.CommentNotFoundException;
import com.roman.sapun.java.socialmedia.exception.InvalidPageSizeException;
import com.roman.sapun.java.socialmedia.exception.UserNotFoundException;
import com.roman.sapun.java.socialmedia.exception.UserStatisticsNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;


public interface CommentService {
    /**
     * <p>creates comment, adds a creation time to it and bonds it to author that created comment.</p>
     *
     * @param requestCommentDTO includes essential parts of comment as title and description for creating comment.
     * @param authentication    for searching currently logged-in user.
     * @return comment DTO that includes author image and comment details.
     */
    CommentDTO createComment(RequestCommentDTO requestCommentDTO, Authentication authentication, HttpServletRequest request) throws CommentNotFoundException, UserNotFoundException, UserStatisticsNotFoundException;

    /**
     * <p>Deletes comment based on comment identifier.</p>
     * @param identifier comment's generated unique identifier.
     * @param authentication for searching currently logged-in user.
     * @return comment DTO that includes author image and comment details.
     * @throws CommentNotFoundException if comment cannot be found or if author and user doesn't match.
     */
    ResponseCommentDTO deleteComment(String identifier, Authentication authentication) throws CommentNotFoundException, UserNotFoundException;

    /**
     * <p>Gets comments based on post identifier and page that we want to return.</p>
     *
     * @param identifier comment's generated unique identifier.
     * @param pageNumber page that we want to return.
     * @return map that includes 50 comments, overall number of comments, current comment page and overall number of pages.
     */
    CommentPageDTO getCommentsByPostIdentifier(String identifier, int pageNumber) throws CommentNotFoundException;

    /**
     * <p>Updates comment by identifier.</p>
     *
     * @param requestCommentDTO includes updated data, such as title and description.
     * @param identifier comment's generated unique identifier.
     * @param authentication authentication for searching currently logged-in user.
     * @return comment DTO that includes author image and updated comment details.
     * @throws CommentNotFoundException if comment cannot be found or if author and user doesn't match.
     */
    ResponseCommentDTO updateCommentById(RequestCommentDTO requestCommentDTO, String identifier, Authentication authentication) throws CommentNotFoundException, UserNotFoundException;

    CommentPageDTO getSavedComments(Authentication authentication, int pageNumber) throws UserNotFoundException, InvalidPageSizeException;

    boolean findSavedCommentByIdentifier(String identifier, Authentication authentication) throws UserNotFoundException;

    ResponseCommentDTO removeCommentFromSavedList(String identifier, Authentication authentication) throws  UserNotFoundException, CommentNotFoundException;

    ResponseCommentDTO addCommentToSavedList(String identifier, Authentication authentication) throws UserNotFoundException, CommentNotFoundException;
}
