package codingtechniques.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import codingtechniques.model.Notification;

public interface NotificationRepository extends CrudRepository<Notification, Long> {
    List<Notification> findByRecipientUserOrderByCreatedAtDesc(String recipientUser);
    List<Notification> findByRecipientUserAndReadFalseOrderByCreatedAtDesc(String recipientUser);
    List<Notification> findByPostId(Long postId);
    List<Notification> findByCommentId(Long commentId);
}