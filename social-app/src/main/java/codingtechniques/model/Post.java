package codingtechniques.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "users")
    private String user;

    @Column(name = "contents", columnDefinition = "TEXT")
    private String content;

    @Column(name = "content_url")
    private String contentUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type")
    private PostType postType = PostType.TEXT;

    @Column(name = "post-like")
    private int like;

    @Column(name = "post-unlike")
    private int unlike;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "postId")
    List<Comment> comments = new ArrayList<>();

    @Column(name = "liked_by_users")
    private String likedByUsers = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    // New fields
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ElementCollection
    @CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @Column(name = "is_pinned")
    private boolean isPinned = false;

    public Post() {
        super();
    }

    public Post(String user, String content, PostType postType, String contentUrl, int like, int unlike, List<Comment> comments) {
        super();
        this.user = user;
        this.content = content;
        this.postType = postType;
        this.contentUrl = contentUrl;
        this.like = like;
        this.unlike = unlike;
        this.comments = comments;
    }

    // Existing getters and setters...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public PostType getPostType() {
        return postType;
    }

    public void setPostType(PostType postType) {
        this.postType = postType;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getUnlike() {
        return unlike;
    }

    public void setUnlike(int unlike) {
        this.unlike = unlike;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(String likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    // New getters and setters
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean isPinned) {
        this.isPinned = isPinned;
    }

    // Existing utility methods
    public void addLikedByUser(String username) {
        if (likedByUsers == null || likedByUsers.isEmpty()) {
            likedByUsers = username;
        } else if (!likedByUsers.contains(username)) {
            likedByUsers += "," + username;
        }
    }

    public boolean isLikedByUser(String username) {
        if (likedByUsers == null || likedByUsers.isEmpty()) {
            return false;
        }
        return likedByUsers.contains(username);
    }

    // New utility methods
    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public void removeTag(String tag) {
        this.tags.remove(tag);
    }
}