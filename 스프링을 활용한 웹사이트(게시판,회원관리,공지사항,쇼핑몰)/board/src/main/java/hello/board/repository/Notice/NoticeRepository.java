package hello.board.repository.Notice;

import org.springframework.data.jpa.repository.JpaRepository;

import hello.board.domain.Notice;
//공지사항 기능을 위한 Spring Data JPA 인터페이스
public interface NoticeRepository extends JpaRepository<Notice, Long>{

}
