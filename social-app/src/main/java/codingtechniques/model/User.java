package codingtechniques.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningPlan> learningPlans = new ArrayList<>();

    // Constructors, getters, and setters
    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<LearningPlan> getLearningPlans() {
        return learningPlans;
    }

    public void setLearningPlans(List<LearningPlan> learningPlans) {
        this.learningPlans = learningPlans;
    }

    // Convenience methods
    public void addLearningPlan(LearningPlan plan) {
        learningPlans.add(plan);
        plan.setUser(this);
    }

    public void removeLearningPlan(LearningPlan plan) {
        learningPlans.remove(plan);
        plan.setUser(null);
    }
}