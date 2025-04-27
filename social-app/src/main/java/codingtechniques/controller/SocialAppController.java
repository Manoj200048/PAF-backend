package codingtechniques.controller;

import org.hibernate.FetchNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import codingtechniques.model.Comment;
import codingtechniques.model.Post;
import codingtechniques.repository.PostRepository;
import codingtechniques.repository.CommentRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/social/api")
@CrossOrigin(origins = "http://localhost:3000")
public class SocialAppController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/posts")
    public Iterable<Post> getAllPost() {
        return postRepository.findAll();
    }

    @PostMapping("/add-post")
    public Post savePost(@RequestBody Post post) {
        return postRepository.save(post);
    }

    // Update post
    @PutMapping("/update-post/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post postDetails) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new FetchNotFoundException("Post", id));

        post.setContent(postDetails.getContent());
        // Only update fields you want to allow updating
        // We're not changing user, like or unlike counts here

        Post updatedPost = postRepository.save(post);
        return ResponseEntity.ok(updatedPost);
    }

    // Delete post
    @DeleteMapping("/delete-post/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new FetchNotFoundException("Post", id));

        postRepository.delete(post);
        return ResponseEntity.ok("Post with id " + id + " deleted successfully");
    }

    @PutMapping("/likes/{id}/{like}")
    public ResponseEntity<Post> updateLike(@PathVariable Long id, @PathVariable int like) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new FetchNotFoundException("Post", id));

        post.setLike(like + 1);

        Post postUpdated = postRepository.save(post);

        return ResponseEntity.ok(postUpdated);
    }

    @PutMapping("/unlikes/{id}/{unlike}")
    public ResponseEntity<Post> updateUnlike(@PathVariable Long id, @PathVariable int unlike) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new FetchNotFoundException("Post", id));

        post.setUnlike(unlike + 1);

        Post postUpdated = postRepository.save(post);

        return ResponseEntity.ok(postUpdated);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new FetchNotFoundException("Post", id));
        return ResponseEntity.ok(post);
    }

    @PutMapping("/comment/{id}")
    public ResponseEntity<Post> updateComment(@PathVariable Long id, @RequestBody Comment comment) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new FetchNotFoundException("Post", id));

        post.getComments().add(comment);

        Post postUpdated = postRepository.save(post);

        return ResponseEntity.ok(postUpdated);
    }

    // Update an existing comment
    @PutMapping("/update-comment/{postId}/{commentId}")
    public ResponseEntity<Post> updateExistingComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody Comment commentDetails) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new FetchNotFoundException("Post", postId));

        List<Comment> comments = post.getComments();
        boolean commentFound = false;

        for (Comment c : comments) {
            if (c.getId().equals(commentId)) {
                c.setContent(commentDetails.getContent());
                // We don't update the user for security reasons
                commentFound = true;
                break;
            }
        }

        if (!commentFound) {
            throw new FetchNotFoundException("Comment", commentId);
        }

        Post updatedPost = postRepository.save(post);
        return ResponseEntity.ok(updatedPost);
    }

    // Delete a comment
    @DeleteMapping("/delete-comment/{postId}/{commentId}")
    public ResponseEntity<Post> deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new FetchNotFoundException("Post", postId));

        List<Comment> comments = post.getComments();
        boolean commentRemoved = comments.removeIf(comment -> comment.getId().equals(commentId));

        if (!commentRemoved) {
            throw new FetchNotFoundException("Comment", commentId);
        }

        Post updatedPost = postRepository.save(post);
        return ResponseEntity.ok(updatedPost);
    }
}