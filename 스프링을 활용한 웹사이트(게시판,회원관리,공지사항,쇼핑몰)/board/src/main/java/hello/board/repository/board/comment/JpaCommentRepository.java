package hello.board.repository.board.comment;

import java.util.List;

import org.springframework.stereotype.Repository;

import hello.board.domain.Comment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
//댓글 기능을 위한 JPA Repository
@Repository
@RequiredArgsConstructor
public class JpaCommentRepository {
	private final EntityManager em;
	
	//boardId를 받아서 해당 게시글의 댓글을 조회 하는 매서드
	@SuppressWarnings("unchecked")
	public List<Comment> findByBoardId(Long id) {
		Query query = em.createQuery("select c from Comment c where boardId=:id");
		query.setParameter("id", id);
		return query.getResultList();
	}
}
