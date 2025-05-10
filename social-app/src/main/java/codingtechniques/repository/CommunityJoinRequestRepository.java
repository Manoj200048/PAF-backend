package codingtechniques.repository;

import codingtechniques.model.CommunityJoinRequest;
import codingtechniques.model.JoinRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityJoinRequestRepository extends JpaRepository<CommunityJoinRequest, Long> {

    List<CommunityJoinRequest> findByCommunityIdAndStatus(Long communityId, JoinRequestStatus status);

    List<CommunityJoinRequest> findByUsernameAndStatus(String username, JoinRequestStatus status);

    Optional<CommunityJoinRequest> findByCommunityIdAndUsername(Long communityId, String username);
}