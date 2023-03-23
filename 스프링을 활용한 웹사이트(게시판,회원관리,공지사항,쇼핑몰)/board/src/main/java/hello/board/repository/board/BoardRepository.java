package hello.board.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;

import hello.board.domain.Board;
//게시판 게시글을 위한 Spring Data JPA 인터페이스
public interface BoardRepository extends JpaRepository<Board, Long>{
}
