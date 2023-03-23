package hello.board.repository.board.like;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
@Repository
@RequiredArgsConstructor
public class JpaLikeRepository {
	private final EntityManager em;
	
	public Long findIdByMemberAndBoard(Long memberId, Long boardId) {
		Query query = em.createQuery("select l.id from Likes l join l.member m join l.board b where m.id = :memberId and b.id = :boardId");
		query.setParameter("memberId", memberId);
		query.setParameter("boardId", boardId);
		return (Long)query.getResultList().stream().findAny().orElse(null);
	}
}
