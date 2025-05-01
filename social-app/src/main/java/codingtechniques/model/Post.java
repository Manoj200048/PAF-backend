package codingtechniques.model;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "users")
    private String user;

    @Column(name = "contents")
    private String content;

    // New field for content URL (for photos and videos)
    @Column(name = "content_url")
    private String contentUrl;

    // Enum for post type
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

    // Track users who liked this post
    @Column(name = "liked_by_users")
    private String likedByUsers = "";  // Stored as comma-separated usernames

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
}