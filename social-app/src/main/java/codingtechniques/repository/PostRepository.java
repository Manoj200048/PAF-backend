package codingtechniques.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import codingtechniques.model.Post;
import codingtechniques.model.PostType;

public interface PostRepository extends CrudRepository<Post, Long> {
    List<Post> findByPostType(PostType postType);
}