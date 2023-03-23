package hello.board.repository.board.adminComment;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
//관리자 답글을 위한 JPA Repository 사용되는 매서드가 없는 상태.
@Repository
@RequiredArgsConstructor
public class JpaAdminCommentRepository {
	
	private final EntityManager em;
}
