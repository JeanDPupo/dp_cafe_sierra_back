package edu.unimagdalena.cowork.api;

import edu.unimagdalena.cowork.api.dto.CommentDtos;
import edu.unimagdalena.cowork.domain.services.CommentService;
import edu.unimagdalena.cowork.shared.security.SecurityUtils;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/products/{id}/comments")
    public CommentDtos.CommentResponse create(@PathVariable Long id, @Valid @RequestBody CommentDtos.CommentCreateRequest request) {
        return commentService.create(SecurityUtils.currentUserId(), id, request);
    }

    @GetMapping("/products/{id}/comments")
    public List<CommentDtos.CommentResponse> list(@PathVariable Long id) {
        return commentService.getVisibleByProduct(id);
    }

    @PatchMapping("/comments/{id}")
    public CommentDtos.CommentResponse update(@PathVariable Long id, @Valid @RequestBody CommentDtos.CommentUpdateRequest request) {
        return commentService.update(SecurityUtils.currentUserId(), id, request);
    }

    @DeleteMapping("/comments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        commentService.delete(SecurityUtils.currentUserId(), id);
    }
}
