package edu.unimagdalena.cowork.domain.services;

import edu.unimagdalena.cowork.api.dto.CommentDtos;
import edu.unimagdalena.cowork.domain.entities.Comment;
import edu.unimagdalena.cowork.domain.entities.Product;
import edu.unimagdalena.cowork.domain.entities.User;
import edu.unimagdalena.cowork.domain.exception.ForbiddenOperationException;
import edu.unimagdalena.cowork.domain.exception.ResourceNotFoundException;
import edu.unimagdalena.cowork.domain.repositories.CommentRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ProductService productService;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, ProductService productService, UserService userService) {
        this.commentRepository = commentRepository;
        this.productService = productService;
        this.userService = userService;
    }

    @Transactional
    public CommentDtos.CommentResponse create(Long userId, Long productId, CommentDtos.CommentCreateRequest request) {
        Product product = productService.getEntityById(productId);
        User user = userService.getById(userId);
        Comment comment = new Comment();
        comment.setProduct(product);
        comment.setAuthorUser(user);
        comment.setRating(request.rating());
        comment.setContent(request.content());
        comment.setVisible(true);
        commentRepository.save(comment);
        return toResponse(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDtos.CommentResponse> getVisibleByProduct(Long productId) {
        return commentRepository.findByProductIdAndVisibleTrueOrderByCreatedAtDesc(productId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CommentDtos.CommentResponse update(Long userId, Long commentId, CommentDtos.CommentUpdateRequest request) {
        Comment comment = getEntity(commentId);
        assertOwner(userId, comment);
        if (request.rating() != null) comment.setRating(request.rating());
        if (request.content() != null) comment.setContent(request.content());
        if (request.visible() != null) comment.setVisible(request.visible());
        commentRepository.save(comment);
        return toResponse(comment);
    }

    @Transactional
    public void delete(Long userId, Long commentId) {
        Comment comment = getEntity(commentId);
        assertOwner(userId, comment);
        commentRepository.delete(comment);
    }

    private Comment getEntity(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado"));
    }

    private void assertOwner(Long userId, Comment comment) {
        if (!comment.getAuthorUser().getId().equals(userId)) {
            throw new ForbiddenOperationException("No puedes modificar un comentario ajeno");
        }
    }

    private CommentDtos.CommentResponse toResponse(Comment comment) {
        return new CommentDtos.CommentResponse(
                comment.getId(),
                comment.getAuthorUser().getId(),
                comment.getAuthorUser().getFullName(),
                comment.getRating(),
                comment.getContent(),
                comment.isVisible(),
                comment.getCreatedAt()
        );
    }
}
