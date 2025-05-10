package codingtechniques.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String user;

    @NotBlank(message = "Comment cannot be empty")
    @Size(min = 1, max = 500, message = "Comment must be between 1 and 500 characters")
    @Pattern(regexp = "^[\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Sm}\\p{Sc}\\p{Sk}]*$",
            message = "Comment contains invalid characters")
    private String content;

    public Comment() {
        super();
    }

    public Comment(String user, String content) {
        super();
        this.user = user;
        this.content = content;
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
}