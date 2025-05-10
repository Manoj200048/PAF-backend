package codingtechniques.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import codingtechniques.model.Post;
import codingtechniques.model.PostType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends CrudRepository<Post, Long> {
    List<Post> findByPostType(PostType postType);

    // Community-related queries
    List<Post> findByCommunityId(Long communityId);

    @Query("SELECT p FROM Post p WHERE p.community.id = :communityId ORDER BY p.createdAt DESC")
    List<Post> findByCommunityIdOrderByCreatedAtDesc(@Param("communityId") Long communityId);

    @Query("SELECT p FROM Post p WHERE p.community.id = :communityId AND p.isPinned = true")
    List<Post> findPinnedPostsByCommunityId(@Param("communityId") Long communityId);

    @Query("SELECT p FROM Post p WHERE p.community.id = :communityId AND :tag MEMBER OF p.tags")
    List<Post> findByCommunityIdAndTag(@Param("communityId") Long communityId, @Param("tag") String tag);

    @Query("SELECT p FROM Post p WHERE p.user = :username")
    List<Post> findByUser(@Param("username") String username);
}