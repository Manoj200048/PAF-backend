package codingtechniques.service;

import codingtechniques.model.LearningPlan;
import codingtechniques.model.User;
import codingtechniques.repository.LearningPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LearningPlanService {

    private final LearningPlanRepository learningPlanRepository;

    @Autowired
    public LearningPlanService(LearningPlanRepository learningPlanRepository) {
        this.learningPlanRepository = learningPlanRepository;
    }

    // Create
    public LearningPlan createLearningPlan(LearningPlan learningPlan) {
        return learningPlanRepository.save(learningPlan);
    }

    // Read (single)
    public LearningPlan getLearningPlanById(Long id) {
        return learningPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LearningPlan not found with id: " + id));
    }

    // Read (all)
    public List<LearningPlan> getAllLearningPlans() {
        return learningPlanRepository.findAll();
    }

    // Read (by user)
    public List<LearningPlan> getLearningPlansByUser(User user) {
        return learningPlanRepository.findByUser(user);
    }

    // Read (by user ID)
    public List<LearningPlan> getLearningPlansByUserId(Long userId) {
        return learningPlanRepository.findByUserId(userId);
    }

    // Update
    public LearningPlan updateLearningPlan(Long id, LearningPlan learningPlanDetails) {
        LearningPlan learningPlan = getLearningPlanById(id);

        learningPlan.setTopic(learningPlanDetails.getTopic());
        learningPlan.setResources(learningPlanDetails.getResources());
        learningPlan.setTimeline(learningPlanDetails.getTimeline());
        learningPlan.setDescription(learningPlanDetails.getDescription());
        learningPlan.setSteps(learningPlanDetails.getSteps());
        // Note: We're not updating the user relationship here

        return learningPlanRepository.save(learningPlan);
    }

    // Delete
    public void deleteLearningPlan(Long id) {
        LearningPlan learningPlan = getLearningPlanById(id);
        learningPlanRepository.delete(learningPlan);
    }
}