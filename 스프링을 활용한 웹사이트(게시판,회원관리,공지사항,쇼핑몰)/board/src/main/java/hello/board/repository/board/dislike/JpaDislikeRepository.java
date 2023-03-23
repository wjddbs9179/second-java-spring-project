package hello.board.repository.board.dislike;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaDislikeRepository {
	private final EntityManager em;
	public Long findIdByMemberAndBoard(Long memberId, Long boardId) {
		Query query = em.createQuery("select d.id from Dislike d join d.member m join d.board b where m.id = :memberId and b.id = :boardId");
		query.setParameter("memberId", memberId);
		query.setParameter("boardId", boardId);
		return (Long)query.getResultList().stream().findAny().orElse(null);
	}

}
