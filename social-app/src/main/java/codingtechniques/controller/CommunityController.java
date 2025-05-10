package codingtechniques.controller;

import codingtechniques.model.*;
import codingtechniques.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/social/api/communities")
@CrossOrigin(origins = "http://localhost:3000")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @Autowired
    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    // Create a new community
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Community> createCommunity(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("isPrivate") boolean isPrivate,
            @RequestParam(value = "topics", required = false) List<String> topics,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile) {

        try {
            Community community = communityService.createCommunity(
                    name,
                    description,
                    "currentUser", // Replace with actual username from auth
                    isPrivate,
                    topics,
                    avatarFile
            );
            return ResponseEntity.ok(community);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get all communities
    @GetMapping
    public ResponseEntity<List<Community>> getAllCommunities() {
        List<Community> communities = communityService.getAllCommunities();
        return ResponseEntity.ok(communities);
    }

    // Get community by ID
    @GetMapping("/{id}")
    public ResponseEntity<Community> getCommunityById(@PathVariable Long id) {
        Community community = communityService.getCommunityById(id);
        return ResponseEntity.ok(community);
    }

    // Get community by name
    @GetMapping("/name/{name}")
    public ResponseEntity<Community> getCommunityByName(@PathVariable String name) {
        Community community = communityService.getCommunityByName(name);
        return ResponseEntity.ok(community);
    }

    // Get communities by owner
    @GetMapping("/owner/{username}")
    public ResponseEntity<List<Community>> getCommunitiesByOwner(@PathVariable String username) {
        List<Community> communities = communityService.getCommunitiesByOwner(username);
        return ResponseEntity.ok(communities);
    }

    // Get communities by member
    @GetMapping("/member/{username}")
    public ResponseEntity<List<Community>> getCommunitiesByMember(@PathVariable String username) {
        List<Community> communities = communityService.getCommunitiesByMember(username);
        return ResponseEntity.ok(communities);
    }

    // Search communities
    @GetMapping("/search")
    public ResponseEntity<List<Community>> searchCommunities(@RequestParam String term) {
        List<Community> communities = communityService.searchCommunities(term);
        return ResponseEntity.ok(communities);
    }

    // Update community
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Community> updateCommunity(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("isPrivate") boolean isPrivate,
            @RequestParam(value = "topics", required = false) List<String> topics,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile) {

        try {
            Community updatedCommunity = communityService.updateCommunity(
                    id,
                    name,
                    description,
                    isPrivate,
                    topics,
                    avatarFile
            );
            return ResponseEntity.ok(updatedCommunity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Delete community
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCommunity(@PathVariable Long id) {
        try {
            communityService.deleteCommunity(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Add member to community
    @PostMapping("/{id}/members/{username}/role")
    public ResponseEntity<CommunityMembership> addMemberWithRole(
            @PathVariable Long id,
            @PathVariable String username,
            @RequestBody Map<String, String> roleRequest) {

        CommunityRole role = CommunityRole.valueOf(roleRequest.get("role").toUpperCase());
        CommunityMembership membership = communityService.addMemberWithRole(id, username, role);
        return ResponseEntity.ok(membership);
    }

    // Remove member from community
    @DeleteMapping("/{id}/members/{username}/complete")
    public ResponseEntity<String> removeMemberCompletely(@PathVariable Long id, @PathVariable String username) {
        communityService.removeMemberCompletely(id, username);
        return ResponseEntity.ok("Member removed completely from community");
    }

    @GetMapping("/{id}/join-requests/pending")
    public ResponseEntity<List<CommunityJoinRequest>> getPendingJoinRequests(@PathVariable Long id) {
        List<CommunityJoinRequest> requests = communityService.getPendingJoinRequests(id);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/join-requests/{requestId}/approve")
    public ResponseEntity<CommunityMembership> approveJoinRequest(@PathVariable Long requestId) {
        CommunityMembership membership = communityService.approveJoinRequest(requestId);
        return ResponseEntity.ok(membership);
    }

    @PostMapping("/join-requests/{requestId}/reject")
    public ResponseEntity<CommunityJoinRequest> rejectJoinRequest(@PathVariable Long requestId) {
        CommunityJoinRequest request = communityService.rejectJoinRequest(requestId);
        return ResponseEntity.ok(request);
    }

    // Get all posts in a community
    @PostMapping("/{id}/posts")
    public ResponseEntity<Post> createCommunityPost(
            @PathVariable Long id,
            @RequestBody Post post) {

        Post createdPost = communityService.createCommunityPost(id, post);
        return ResponseEntity.ok(createdPost);
    }

    @PutMapping("/{communityId}/posts/{postId}/pin")
    public ResponseEntity<Post> pinCommunityPost(
            @PathVariable Long communityId,
            @PathVariable Long postId) {

        Post pinnedPost = communityService.pinCommunityPost(communityId, postId);
        return ResponseEntity.ok(pinnedPost);
    }

    @PutMapping("/{communityId}/posts/{postId}/unpin")
    public ResponseEntity<Post> unpinCommunityPost(
            @PathVariable Long communityId,
            @PathVariable Long postId) {

        Post unpinnedPost = communityService.unpinCommunityPost(communityId, postId);
        return ResponseEntity.ok(unpinnedPost);
    }

    @GetMapping("/{id}/posts/pinned")
    public ResponseEntity<List<Post>> getPinnedPosts(@PathVariable Long id) {
        List<Post> pinnedPosts = communityService.getPinnedPosts(id);
        return ResponseEntity.ok(pinnedPosts);
    }

    @GetMapping("/{id}/moderators")
    public ResponseEntity<List<CommunityMembership>> getCommunityModerators(@PathVariable Long id) {
        List<CommunityMembership> moderators = communityService.getCommunityModerators(id);
        return ResponseEntity.ok(moderators);
    }

    @GetMapping("/{id}/top-contributors")
    public ResponseEntity<List<CommunityMembership>> getTopContributors(@PathVariable Long id) {
        List<CommunityMembership> contributors = communityService.getTopContributors(id);
        return ResponseEntity.ok(contributors);
    }

    // Add topic to community
    @PostMapping("/{id}/topics")
    public ResponseEntity<Community> addTopic(@PathVariable Long id, @RequestParam String topic) {
        try {
            Community updatedCommunity = communityService.addTopicToCommunity(id, topic);
            return ResponseEntity.ok(updatedCommunity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Remove topic from community
    @DeleteMapping("/{id}/topics/{topic}")
    public ResponseEntity<Community> removeTopic(@PathVariable Long id, @PathVariable String topic) {
        try {
            Community updatedCommunity = communityService.removeTopicFromCommunity(id, topic);
            return ResponseEntity.ok(updatedCommunity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}