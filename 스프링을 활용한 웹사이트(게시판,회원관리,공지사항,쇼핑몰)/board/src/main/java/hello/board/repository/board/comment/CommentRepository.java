package hello.board.repository.board.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import hello.board.domain.Comment;
//댓글기능을 위한 Spring Data JPA 인터페이스
public interface CommentRepository extends JpaRepository<Comment, Long>{

}
