package codingtechniques.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "learning_plans")
public class LearningPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topic;

    @ElementCollection
    @CollectionTable(name = "learning_plan_resources", joinColumns = @JoinColumn(name = "learning_plan_id"))
    @Column(name = "resource")
    private List<String> resources;

    @Column(name = "timeline_minutes")
    private int timeline; // in minutes

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "learning_plan_steps", joinColumns = @JoinColumn(name = "learning_plan_id"))
    @Column(name = "step")
    private List<String> steps;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructors
    public LearningPlan() {
        // Default constructor
    }

    public LearningPlan(String topic, List<String> resources, int timeline,
                        String description, List<String> steps, User user) {
        this.topic = topic;
        this.resources = resources;
        this.timeline = timeline;
        this.description = description;
        this.steps = steps;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    public int getTimeline() {
        return timeline;
    }

    public void setTimeline(int timeline) {
        this.timeline = timeline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // toString method
    @Override
    public String toString() {
        return "LearningPlan{" +
                "id=" + id +
                ", topic='" + topic + '\'' +
                ", resources=" + resources +
                ", timeline=" + timeline +
                ", description='" + description + '\'' +
                ", steps=" + steps +
                ", user=" + (user != null ? user.getId() : "null") +
                '}';
    }
}