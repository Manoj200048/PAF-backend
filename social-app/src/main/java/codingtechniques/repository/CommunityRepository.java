package codingtechniques.repository;

import codingtechniques.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    Optional<Community> findByName(String name);

    @Query("SELECT c FROM Community c WHERE :username = c.ownerUsername")
    List<Community> findByOwnerUsername(@Param("username") String username);

    @Query("SELECT c FROM Community c WHERE :username MEMBER OF c.members")
    List<Community> findByMemberUsername(@Param("username") String username);

    @Query("SELECT c FROM Community c WHERE :topic MEMBER OF c.topics")
    List<Community> findByTopic(@Param("topic") String topic);

    // Search communities by name or description containing the search term
    @Query("SELECT c FROM Community c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Community> searchCommunities(@Param("searchTerm") String searchTerm);
}