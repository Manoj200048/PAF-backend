package codingtechniques.controller;

import org.hibernate.FetchNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import codingtechniques.model.Comment;
import codingtechniques.model.Notification;
import codingtechniques.model.NotificationType;
import codingtechniques.model.Post;
import codingtechniques.model.PostType;
import codingtechniques.repository.PostRepository;
import codingtechniques.repository.CommentRepository;
import codingtechniques.repository.NotificationRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/social/api")
@CrossOrigin(origins = "http://localhost:3000")
public class SocialAppController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/posts")
    public Iterable<Post> getAllPost() {
        return postRepository.findAll();
    }

    @GetMapping("/posts/type/{type}")
    public Iterable<Post> getPostsByType(@PathVariable String type) {
        return postRepository.findByPostType(PostType.valueOf(type.toUpperCase()));
    }

    @PostMapping("/add-post")
    public Post savePost(@RequestBody Post post) {
        return postRepository.save(post);
    }

    // Update post
    @PutMapping("/update-post/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post postDetails) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new FetchNotFoundException("Post", id));

        post.setContent(postDetails.getContent());

        // Update media content if provided
        if (postDetails.getContentUrl() != null) {
            post.setContentUrl(postDetails.getContentUrl());
        }
        if (postDetails.getPostType() != null) {
            post.setPostType(postDetails.getPostType());
        }

        Post updatedPost = postRepository.save(post);
        return ResponseEntity.ok(updatedPost);
    }

    // Delete post
    @DeleteMapping("/delete-post/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new FetchNotFoundException("Post", id));

        // Also delete associated notifications
        notificationRepository.deleteAll(
                notificationRepository.findByPostId(id)
        );

        postRepository.delete(post);
        return ResponseEntity.ok("Post with id " + id + " deleted successfully");
    }

    @PutMapping("/likes/{id}")
    public ResponseEntity<Post> updateLike(
            @PathVariable Long id,
            @RequestParam String username) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new FetchNotFoundException("Post", id));

        // Check if user already liked this post
        if (!post.isLikedByUser(username)) {
            post.setLike(post.getLike() + 1);
            post.addLikedByUser(username);

            // Create notification for post owner
            if (!username.equals(post.getUser())) {
                Notification notification = new Notification(
                        post.getUser(),  // recipient (post owner)
                        username,        // sender
                        post.getId(),    // post ID
                        null,            // no comment ID
                        NotificationType.POST_LIKE
                );
                notificationRepository.save(notification);
            }
        }

        Post postUpdated = postRepository.save(post);
        return ResponseEntity.ok(postUpdated);
    }

    @PutMapping("/unlikes/{id}/{unlike}")
    public ResponseEntity<Post> updateUnlike(@PathVariable Long id, @PathVariable int unlike) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new FetchNotFoundException("Post", id));

        post.setUnlike(unlike + 1);

        Post postUpdated = postRepository.save(post);

        return ResponseEntity.ok(postUpdated);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new FetchNotFoundException("Post", id));
        return ResponseEntity.ok(post);
    }

    @PutMapping("/comment/{id}")
    public ResponseEntity<Post> updateComment(
            @PathVariable Long id,
            @RequestBody Comment comment) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new FetchNotFoundException("Post", id));

        // Save the comment to get its ID
        Comment savedComment = commentRepository.save(comment);
        post.getComments().add(savedComment);

        // Create notification for post owner
        if (!comment.getUser().equals(post.getUser())) {
            Notification notification = new Notification(
                    post.getUser(),      // recipient (post owner)
                    comment.getUser(),   // sender
                    post.getId(),        // post ID
                    savedComment.getId(), // comment ID
                    NotificationType.POST_COMMENT
            );
            notificationRepository.save(notification);
        }

        Post postUpdated = postRepository.save(post);
        return ResponseEntity.ok(postUpdated);
    }

    // Update an existing comment
    @PutMapping("/update-comment/{postId}/{commentId}")
    public ResponseEntity<Post> updateExistingComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody Comment commentDetails) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new FetchNotFoundException("Post", postId));

        List<Comment> comments = post.getComments();
        boolean commentFound = false;

        for (Comment c : comments) {
            if (c.getId().equals(commentId)) {
                c.setContent(commentDetails.getContent());
                // We don't update the user for security reasons
                commentFound = true;
                break;
            }
        }

        if (!commentFound) {
            throw new FetchNotFoundException("Comment", commentId);
        }

        Post updatedPost = postRepository.save(post);
        return ResponseEntity.ok(updatedPost);
    }

    // Delete a comment
    @DeleteMapping("/delete-comment/{postId}/{commentId}")
    public ResponseEntity<Post> deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new FetchNotFoundException("Post", postId));

        List<Comment> comments = post.getComments();
        boolean commentRemoved = comments.removeIf(comment -> comment.getId().equals(commentId));

        if (!commentRemoved) {
            throw new FetchNotFoundException("Comment", commentId);
        }

        // Delete associated notifications
        notificationRepository.deleteAll(
                notificationRepository.findByCommentId(commentId)
        );

        Post updatedPost = postRepository.save(post);
        return ResponseEntity.ok(updatedPost);
    }

    // Notification endpoints
    @GetMapping("/notifications/{username}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable String username) {
        List<Notification> notifications = notificationRepository.findByRecipientUserOrderByCreatedAtDesc(username);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/notifications/{username}/unread")
    public ResponseEntity<List<Notification>> getUserUnreadNotifications(@PathVariable String username) {
        List<Notification> notifications = notificationRepository.findByRecipientUserAndReadFalseOrderByCreatedAtDesc(username);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/notifications/{notificationId}/mark-read")
    public ResponseEntity<Notification> markNotificationAsRead(@PathVariable Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new FetchNotFoundException("Notification", notificationId));

        notification.setRead(true);
        notification = notificationRepository.save(notification);

        return ResponseEntity.ok(notification);
    }

    @PutMapping("/notifications/{username}/mark-all-read")
    public ResponseEntity<String> markAllNotificationsAsRead(@PathVariable String username) {
        List<Notification> notifications = notificationRepository.findByRecipientUserAndReadFalseOrderByCreatedAtDesc(username);

        for (Notification notification : notifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }

        return ResponseEntity.ok("All notifications marked as read for user: " + username);
    }
}