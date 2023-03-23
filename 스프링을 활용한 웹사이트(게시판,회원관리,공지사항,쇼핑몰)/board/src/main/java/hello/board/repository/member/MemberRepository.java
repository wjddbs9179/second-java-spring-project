package hello.board.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;

import hello.board.domain.Member;
//회원관리를 위한 Spring Data JPA 인터페이스
public interface MemberRepository extends JpaRepository<Member, Long>{
	
}
