package codingtechniques.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "communities")
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "is_private")
    private boolean isPrivate = false;

    @Column(name = "avatar_url")
    private String avatarUrl;

    // Owner of the community
    @Column(name = "owner_username", nullable = false)
    private String ownerUsername;



    // Members of the community (usernames)
    @ElementCollection
    @CollectionTable(name = "community_members", joinColumns = @JoinColumn(name = "community_id"))
    @Column(name = "username")
    private Set<String> members = new HashSet<>();

    // Topics or categories for this community
    @ElementCollection
    @CollectionTable(name = "community_topics", joinColumns = @JoinColumn(name = "community_id"))
    @Column(name = "topic")
    private List<String> topics = new ArrayList<>();

    // Posts in this community
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    // Constructors
    public Community() {
        // Default constructor
    }

    public Community(String name, String description, String ownerUsername, boolean isPrivate) {
        this.name = name;
        this.description = description;
        this.ownerUsername = ownerUsername;
        this.isPrivate = isPrivate;
        this.members = new HashSet<>();
        this.members.add(ownerUsername);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public Set<String> getMembers() {
        return members;
    }

    public void setMembers(Set<String> members) {
        this.members = members;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    // Helper methods
    public void addMember(String username) {
        this.members.add(username);
    }

    public void removeMember(String username) {
        // Don't allow removing the owner
        if (!username.equals(ownerUsername)) {
            this.members.remove(username);
        }
    }

    public boolean isMember(String username) {
        return this.members.contains(username);
    }

    public void addTopic(String topic) {
        if (!this.topics.contains(topic)) {
            this.topics.add(topic);
        }
    }

    public void removeTopic(String topic) {
        this.topics.remove(topic);
    }

    public void addPost(Post post) {
        this.posts.add(post);
        post.setCommunity(this);
    }

    public void removePost(Post post) {
        this.posts.remove(post);
        post.setCommunity(null);
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    

    // toString method
    @Override
    public String toString() {
        return "Community{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", ownerUsername='" + ownerUsername + '\'' +
                ", memberCount=" + members.size() +
                ", topicsCount=" + topics.size() +
                ", postsCount=" + posts.size() +
                '}';
    }
}