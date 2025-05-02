package codingtechniques.controller;

import codingtechniques.model.LearningPlan;
import codingtechniques.service.LearningPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning-plans")
public class LearningPlanController {

    private final LearningPlanService learningPlanService;

    @Autowired
    public LearningPlanController(LearningPlanService learningPlanService) {
        this.learningPlanService = learningPlanService;
    }

    // Create
    @PostMapping
    public LearningPlan createLearningPlan(@RequestBody LearningPlan learningPlan) {
        return learningPlanService.createLearningPlan(learningPlan);
    }

    // Read (single)
    @GetMapping("/{id}")
    public ResponseEntity<LearningPlan> getLearningPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(learningPlanService.getLearningPlanById(id));
    }

    // Read (all)
    @GetMapping
    public List<LearningPlan> getAllLearningPlans() {
        return learningPlanService.getAllLearningPlans();
    }

    // Read (by user ID)
    @GetMapping("/user/{userId}")
    public List<LearningPlan> getLearningPlansByUserId(@PathVariable Long userId) {
        return learningPlanService.getLearningPlansByUserId(userId);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<LearningPlan> updateLearningPlan(
            @PathVariable Long id, @RequestBody LearningPlan learningPlanDetails) {
        return ResponseEntity.ok(learningPlanService.updateLearningPlan(id, learningPlanDetails));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLearningPlan(@PathVariable Long id) {
        learningPlanService.deleteLearningPlan(id);
        return ResponseEntity.ok().build();
    }
}