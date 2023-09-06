package com.roman.sapun.java.socialmedia.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.roman.sapun.java.socialmedia.dto.ResponseExceptionDTO;
import com.roman.sapun.java.socialmedia.dto.notification.CommentNotificationDTO;
import com.roman.sapun.java.socialmedia.dto.notification.NotificationDTO;
import com.roman.sapun.java.socialmedia.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/subscriptions")
    public void createFollowNotification(@RequestBody NotificationDTO notificationDTO) {
        notificationService.createFollowNotification(notificationDTO.message(), notificationDTO.username());
    }

    /**
     * Creates a comment notification and sends it using server send event (SSE).
     * This method is used to notify users about new comments.
     *
     * @param commentNotificationDTO The DTO containing the comment identifier and message for the notification.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/comments")
    public void createCommentNotification(@RequestBody CommentNotificationDTO commentNotificationDTO) {
        notificationService.createCommentNotification(commentNotificationDTO.commentIdentifier(), commentNotificationDTO.message());
    }

    /**
     * Sends a notification message to a Slack channel.
     * This method is typically used for notifying about exceptions or errors in the system.
     *
     * @param responseExceptionDTO The DTO containing the notification message, timestamp, and cause information.
     * @throws JsonProcessingException If there is an issue with processing the JSON request body.
     */
    @PostMapping("/slack")
    public void sendNotificationToSlack(@RequestBody ResponseExceptionDTO responseExceptionDTO) throws JsonProcessingException {
        notificationService.sendMessage(responseExceptionDTO.message(),
                responseExceptionDTO.timestamp().toString(), responseExceptionDTO.causedBy());
    }
    /**
     * Retrieves a list of notifications for a specific user identified by their username.
     * Users can use this method to view their notifications.
     *
     * @param username The username of the user for whom notifications are to be retrieved.
     * @return A list of NotificationDTO objects, each representing a notification.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{username}")
    public List<NotificationDTO> getNotifications(@PathVariable String username) {
        return notificationService.getNotifications(username);
    }
}