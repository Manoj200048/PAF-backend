package codingtechniques.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "community_memberships")
public class CommunityMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "community_id", nullable = false)
    private Long communityId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private CommunityRole role = CommunityRole.MEMBER;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt = LocalDateTime.now();

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt = LocalDateTime.now();

    @Column(name = "contribution_points")
    private int contributionPoints = 0;

    // Constructors
    public CommunityMembership() {
        // Default constructor
    }

    public CommunityMembership(Long communityId, String username, CommunityRole role) {
        this.communityId = communityId;
        this.username = username;
        this.role = role;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public CommunityRole getRole() {
        return role;
    }

    public void setRole(CommunityRole role) {
        this.role = role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public LocalDateTime getLastActiveAt() {
        return lastActiveAt;
    }

    public void setLastActiveAt(LocalDateTime lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }

    public int getContributionPoints() {
        return contributionPoints;
    }

    public void setContributionPoints(int contributionPoints) {
        this.contributionPoints = contributionPoints;
    }

    // Add contribution points
    public void addContributionPoints(int points) {
        this.contributionPoints += points;
    }

    // Update activity
    public void updateActivity() {
        this.lastActiveAt = LocalDateTime.now();
    }
}