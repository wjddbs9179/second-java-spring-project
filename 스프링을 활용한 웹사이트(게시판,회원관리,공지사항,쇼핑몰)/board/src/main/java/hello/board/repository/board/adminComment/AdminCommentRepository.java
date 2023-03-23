package hello.board.repository.board.adminComment;

import org.springframework.data.jpa.repository.JpaRepository;

import hello.board.domain.AdminComment;
//관리자 답글을 위한 Spring Data JPA인터페이스
public interface AdminCommentRepository extends JpaRepository<AdminComment, Long>{

}
