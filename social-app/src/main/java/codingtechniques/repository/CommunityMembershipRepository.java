package codingtechniques.repository;

import codingtechniques.model.CommunityMembership;
import codingtechniques.model.CommunityRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommunityMembershipRepository extends JpaRepository<CommunityMembership, Long> {

    List<CommunityMembership> findByCommunityId(Long communityId);

    List<CommunityMembership> findByUsername(String username);

    Optional<CommunityMembership> findByCommunityIdAndUsername(Long communityId, String username);

    List<CommunityMembership> findByCommunityIdAndRole(Long communityId, CommunityRole role);

    @Query("SELECT cm FROM CommunityMembership cm WHERE cm.communityId = :communityId ORDER BY cm.contributionPoints DESC")
    List<CommunityMembership> findTopContributorsByCommunityId(@Param("communityId") Long communityId);

    void deleteByCommunityIdAndUsername(Long communityId, String username);
}