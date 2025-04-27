package codingtechniques.repository;

import org.springframework.data.repository.CrudRepository;

import codingtechniques.model.Comment;

public interface CommentRepository extends CrudRepository<Comment, Long> {

}