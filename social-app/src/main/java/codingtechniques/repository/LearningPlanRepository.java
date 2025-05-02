package codingtechniques.repository;

import codingtechniques.model.LearningPlan;
import codingtechniques.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LearningPlanRepository extends JpaRepository<LearningPlan, Long> {
    List<LearningPlan> findByUser(User user);
    List<LearningPlan> findByUserId(Long userId);
}