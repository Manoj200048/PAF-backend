package codingtechniques.service;

import codingtechniques.model.*;
import codingtechniques.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final NotificationRepository notificationRepository;
    private final CommunityMembershipRepository membershipRepository;
    private final CommunityJoinRequestRepository joinRequestRepository;
    private final PostRepository postRepository;






    @Autowired
    public CommunityService(
            CommunityRepository communityRepository,
            NotificationRepository notificationRepository,
            CommunityMembershipRepository membershipRepository,
            CommunityJoinRequestRepository joinRequestRepository,
            PostRepository postRepository) {
        this.communityRepository = communityRepository;
        this.notificationRepository = notificationRepository;
        this.membershipRepository = membershipRepository;
        this.joinRequestRepository = joinRequestRepository;
        this.postRepository = postRepository;
    }

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Create a new community
    @Transactional
    public Community createCommunity(
            String name,
            String description,
            String ownerUsername,
            boolean isPrivate,
            List<String> topics,
            MultipartFile avatarFile) throws IOException {

        // Validate inputs
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Community name is required");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }

        // Handle file upload
        String avatarUrl = null;
        if (avatarFile != null && !avatarFile.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + avatarFile.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = avatarFile.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                avatarUrl = "/uploads/" + fileName;
            }
        }

        // Create community
        Community community = new Community(name, description, ownerUsername, isPrivate);

        if (topics != null) {
            community.setTopics(topics.stream()
                    .filter(topic -> topic != null && !topic.trim().isEmpty())
                    .collect(Collectors.toList()));
        }

        if (avatarUrl != null) {
            community.setAvatarUrl(avatarUrl);
        }

        return communityRepository.save(community);
    }

    // Get community by ID
    public Community getCommunityById(Long id) {
        return communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Community not found with id: " + id));
    }

    // Get community by name
    public Community getCommunityByName(String name) {
        return communityRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Community not found with name: " + name));
    }

    // Get all communities
    public List<Community> getAllCommunities() {
        return communityRepository.findAll();
    }

    // Get communities by owner
    public List<Community> getCommunitiesByOwner(String username) {
        return communityRepository.findByOwnerUsername(username);
    }

    // Get communities by member
    public List<Community> getCommunitiesByMember(String username) {
        return communityRepository.findByMemberUsername(username);
    }

    // Search communities
    public List<Community> searchCommunities(String searchTerm) {
        return communityRepository.searchCommunities(searchTerm);
    }

    // Update community details
    @Transactional
    public Community updateCommunity(
            Long id,
            String name,
            String description,
            boolean isPrivate,
            List<String> topics,
            MultipartFile avatarFile) throws IOException {

        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Community not found"));

//        Validate ownership (add your authentication logic)
//        if (!community.getOwnerUsername().equals(currentUser)) {
//            throw new RuntimeException("Only the owner can update this community");
//        }

        community.setName(name);
        community.setDescription(description);
        community.setPrivate(isPrivate);

        if (topics != null) {
            community.setTopics(topics.stream()
                    .filter(topic -> topic != null && !topic.trim().isEmpty())
                    .collect(Collectors.toList()));
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            // Delete old avatar if exists
            if (community.getAvatarUrl() != null) {
                Path oldAvatarPath = Paths.get(uploadDir, community.getAvatarUrl().replace("/uploads/", ""));
                Files.deleteIfExists(oldAvatarPath);
            }

            // Upload new avatar
            String fileName = UUID.randomUUID().toString() + "_" + avatarFile.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = avatarFile.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                community.setAvatarUrl("/uploads/" + fileName);
            }
        }

        return communityRepository.save(community);
    }

    // Delete community

    @Transactional
    public void deleteCommunity(Long id) throws IOException {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Community not found"));

        // Validate ownership (add your authentication logic)
        // if (!community.getOwnerUsername().equals(currentUser)) {
        //     throw new RuntimeException("Only the owner can delete this community");
        // }

        // Delete avatar file if exists
        if (community.getAvatarUrl() != null) {
            Path avatarPath = Paths.get(uploadDir, community.getAvatarUrl().replace("/uploads/", ""));
            Files.deleteIfExists(avatarPath);
        }

        // Delete all related posts and their media
        for (Post post : community.getPosts()) {
            if (post.getContentUrl() != null) {
                Path mediaPath = Paths.get(uploadDir, post.getContentUrl().replace("/uploads/", ""));
                Files.deleteIfExists(mediaPath);
            }
        }

        communityRepository.delete(community);
    }

    // Add member to community
    @Transactional
    public CommunityMembership addMemberWithRole(Long communityId, String username, CommunityRole role) {
        Community community = getCommunityById(communityId);

        // Add to simple members list in Community
        if (!community.isMember(username)) {
            community.addMember(username);
            communityRepository.save(community);

            // Create detailed membership
            CommunityMembership membership = new CommunityMembership(communityId, username, role);
            membership = membershipRepository.save(membership);

            // Send notification to the community owner
            if (!username.equals(community.getOwnerUsername())) {
                Notification notification = new Notification(
                        community.getOwnerUsername(),
                        username,
                        null, // No post ID
                        null, // No comment ID
                        NotificationType.COMMUNITY_JOIN
                );
                notificationRepository.save(notification);
            }

            return membership;
        } else {
            // If already a member, update role
            Optional<CommunityMembership> existingMembership =
                    membershipRepository.findByCommunityIdAndUsername(communityId, username);

            if (existingMembership.isPresent()) {
                CommunityMembership membership = existingMembership.get();
                membership.setRole(role);
                return membershipRepository.save(membership);
            } else {
                // Create new membership record if somehow missing
                CommunityMembership membership = new CommunityMembership(communityId, username, role);
                return membershipRepository.save(membership);
            }
        }
    }

    // Remove member from community
    @Transactional
    public void removeMemberCompletely(Long communityId, String username) {
        Community community = getCommunityById(communityId);

        // Cannot remove the owner
        if (!username.equals(community.getOwnerUsername()) && community.isMember(username)) {
            // Remove from simple members list
            community.removeMember(username);
            communityRepository.save(community);

            // Delete detailed membership
            membershipRepository.deleteByCommunityIdAndUsername(communityId, username);
        }
    }


    @Transactional
    public CommunityMembership approveJoinRequest(Long requestId) {
        CommunityJoinRequest request = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Join request not found with id: " + requestId));

        if (request.getStatus() != JoinRequestStatus.PENDING) {
            throw new RuntimeException("This request has already been " + request.getStatus().toString().toLowerCase());
        }

        // Update request status
        request.setStatus(JoinRequestStatus.APPROVED);
        joinRequestRepository.save(request);

        // Add member to community
        return addMemberWithRole(request.getCommunityId(), request.getUsername(), CommunityRole.MEMBER);
    }


    @Transactional
    public CommunityJoinRequest rejectJoinRequest(Long requestId) {
        CommunityJoinRequest request = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Join request not found with id: " + requestId));

        if (request.getStatus() != JoinRequestStatus.PENDING) {
            throw new RuntimeException("This request has already been " + request.getStatus().toString().toLowerCase());
        }

        // Update request status
        request.setStatus(JoinRequestStatus.REJECTED);
        return joinRequestRepository.save(request);
    }

    // Get pending join requests for a community
    public List<CommunityJoinRequest> getPendingJoinRequests(Long communityId) {
        return joinRequestRepository.findByCommunityIdAndStatus(communityId, JoinRequestStatus.PENDING);
    }

    @Transactional
    public Post createCommunityPost(Long communityId, Post post) {
        Community community = getCommunityById(communityId);

        // Check if user is a member
        if (!community.isMember(post.getUser())) {
            throw new RuntimeException("Only members can post in this community");
        }

        // Set community relationship
        post.setCommunity(community);
        Post savedPost = postRepository.save(post);

        // Update member contribution
        Optional<CommunityMembership> membership =
                membershipRepository.findByCommunityIdAndUsername(communityId, post.getUser());

        if (membership.isPresent()) {
            CommunityMembership member = membership.get();
            member.addContributionPoints(5); // Award points for posting
            member.updateActivity();
            membershipRepository.save(member);
        }

        // Notify community members about new post (optional)
        // This could be resource-intensive for large communities
        // Consider implementing a more sophisticated notification system

        return savedPost;
    }

    @Transactional
    public Post pinCommunityPost(Long communityId, Long postId) {
        // Verify community exists
        getCommunityById(communityId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        if (post.getCommunity() == null || !post.getCommunity().getId().equals(communityId)) {
            throw new RuntimeException("Post does not belong to this community");
        }

        post.setPinned(true);
        return postRepository.save(post);
    }

    public Post unpinCommunityPost(Long communityId, Long postId) {
        // Verify community exists
        getCommunityById(communityId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        if (post.getCommunity() == null || !post.getCommunity().getId().equals(communityId)) {
            throw new RuntimeException("Post does not belong to this community");
        }

        post.setPinned(false);
        return postRepository.save(post);
    }

    public List<Post> getPinnedPosts(Long communityId) {
        // Verify community exists
        getCommunityById(communityId);
        return postRepository.findPinnedPostsByCommunityId(communityId);
    }

    public List<Post> getCommunityPosts(Long communityId) {
        // Verify community exists
        getCommunityById(communityId);
        return postRepository.findByCommunityIdOrderByCreatedAtDesc(communityId);
    }

    // Get community moderators
    public List<CommunityMembership> getCommunityModerators(Long communityId) {
        // Verify community exists
        getCommunityById(communityId);
        return membershipRepository.findByCommunityIdAndRole(communityId, CommunityRole.MODERATOR);
    }

    // Get top contributors in a community
    public List<CommunityMembership> getTopContributors(Long communityId) {
        // Verify community exists
        getCommunityById(communityId);
        return membershipRepository.findTopContributorsByCommunityId(communityId);
    }


    // Add post to community
    @Transactional
    public Community addPost(Long communityId, Post post) {
        Community community = getCommunityById(communityId);

        if (community.isMember(post.getUser())) {
            community.addPost(post);
        }

        return communityRepository.save(community);
    }

    @Transactional
    public Community addTopicToCommunity(Long communityId, String topic) {
        Community community = getCommunityById(communityId);

        // Validate topic
        if (topic == null || topic.trim().isEmpty()) {
            throw new IllegalArgumentException("Topic cannot be empty");
        }

        // Add topic if not already present
        if (!community.getTopics().contains(topic)) {
            community.addTopic(topic);
            return communityRepository.save(community);
        }
        return community;
    }

    @Transactional
    public Community removeTopicFromCommunity(Long communityId, String topic) {
        Community community = getCommunityById(communityId);

        // Remove topic if exists
        if (community.getTopics().contains(topic)) {
            community.removeTopic(topic);
            return communityRepository.save(community);
        }
        return community;
    }
}