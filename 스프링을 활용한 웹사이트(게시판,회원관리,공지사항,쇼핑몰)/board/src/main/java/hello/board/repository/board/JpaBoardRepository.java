package hello.board.repository.board;

import java.util.List;

import org.springframework.stereotype.Repository;

import hello.board.domain.Board;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//게시판 게시글을 위한 JPA Repository
@Slf4j
@Repository
@RequiredArgsConstructor
public class JpaBoardRepository {
	private final EntityManager em;
	//게시판 게시글 리스트를 반환하는 매서드
	@SuppressWarnings("unchecked")
	public List<Board> findAllPage(int page,String field,String query,int maxResult, String sort){
		//검색어의 field에 맞는 쿼리문 작성
		Query jpql = null;

		if(sort.equals("likes")) {
			if(field.equals("subjectAndContent"))
				jpql = em.createQuery("select b from Board b left join b.likes l where (b.subject like concat('%',:query,'%') or b.content like concat('%',:query,'%')) group by b.id order by count(l) desc, b.id desc");
			if(field.equals("subjectAndWriter"))
				jpql = em.createQuery("select b from Board b left join b.likes l where (b.subject like concat('%',:query,'%') or b.writer like concat('%',:query,'%')) group by b.id order by count(l) desc, b.id desc");
			if(field.equals("contentAndWriter"))
				jpql = em.createQuery("select b from Board b left join b.likes l where (b.content like concat('%',:query,'%') or b.writer like concat('%',:query,'%')) group by b.id order by count(l) desc, b.id desc");
			if(field.equals("subjectAndContentAndWriter"))
				jpql = em.createQuery("select b from Board b left join b.likes l where (b.subject like concat('%',:query,'%') or b.content like concat('%',:query,'%')) or b.writer like concat('%',:query,'%') group by b.id order by count(l) desc, b.id desc");
			if(field.equals("subject")||field.equals("content")||field.equals("writer")) 
				jpql = em.createQuery("select b from Board b left join b.likes l where (b."+field+" like concat('%',:query,'%')) group by b.id order by count(l) desc, b.id desc");
		}else {
			if(field.equals("subjectAndContent"))
				jpql = em.createQuery("select b from Board b where subject like concat('%',:query,'%') or content like concat('%',:query,'%') order by "+sort+" desc");
			if(field.equals("subjectAndWriter"))
				jpql = em.createQuery("select b from Board b where subject like concat('%',:query,'%') or writer like concat('%',:query,'%') order by "+sort+" desc");
			if(field.equals("contentAndWriter"))
				jpql = em.createQuery("select b from Board b where content like concat('%',:query,'%') or writer like concat('%',:query,'%') order by "+sort+" desc");
			if(field.equals("subjectAndContentAndWriter"))
				jpql = em.createQuery("select b from Board b where subject like concat('%',:query,'%') or content like concat('%',:query,'%') or writer like concat('%',:query,'%') order by "+sort+" desc");
			if(field.equals("subject")||field.equals("content")||field.equals("writer")) 
				jpql = em.createQuery("select b from Board b where "+field+" like concat('%',:query,'%') order by "+sort+" desc");
		}
		//검색어 세팅
		jpql.setParameter("query", query);
		//몇번째 게시글부터 조회 할건지
		jpql.setFirstResult((page-1)*maxResult);
		//몇개의 레코드를 조회 할건지
		jpql.setMaxResults(maxResult);
		return jpql.getResultList();
	}
	//검색어에 따라 전체 게시글 수를 반환하는 매서드
	public long count(String field, String query) {
		//검색 필드와 검색키워드에 따라 다르게 쿼리문 작성
		Query jpql = null;
		if(field.equals("subjectAndContent"))
			jpql = em.createQuery("select count(*) from Board b where subject like concat('%',:query,'%') or content like concat('%',:query,'%')");
		if(field.equals("subjectAndWriter"))
			jpql = em.createQuery("select count(*) from Board b where subject like concat('%',:query,'%') or writer like concat('%',:query,'%')");
		if(field.equals("contentAndWriter"))
			jpql = em.createQuery("select count(*) from Board b where content like concat('%',:query,'%') or writer like concat('%',:query,'%')");
		if(field.equals("subjectAndContentAndWriter"))
			jpql = em.createQuery("select count(*) from Board b where subject like concat('%',:query,'%') or content like concat('%',:query,'%') or writer like concat('%',:query,'%')");
		if(field.equals("subject")||field.equals("content")||field.equals("writer")) 
			jpql = em.createQuery("select count(*) from Board b where "+field+" like concat('%',:query,'%')");
		//검색키워드 파라미터 세팅
		jpql.setParameter("query", query);
		//게시글 수는 1개의 결과값만 가지기 때문에 stream.findAny.get으로 결과값을 꺼내어 반환 하였음.
		return (long) jpql.getResultList().stream().findAny().get();
	}
	//다음글 조회 기능
	@SuppressWarnings("unchecked")
	public Board findNextBoard(Long boardId) {
		//현재 게시글의 id를 넘겨주면 다음 게시글의 객체를 반환
		Query query = em.createQuery("select b from Board b where id<:boardId order by id desc");
		query.setParameter("boardId", boardId);
		query.setMaxResults(1);

		return (Board)query.getResultList().stream().findAny().orElse(null);
	}
	//이전글 조회 기능
	@SuppressWarnings("unchecked")
	public Board findPrevBoard(Long boardId) {
		//다음글 findNextBoard와 같은 방식으로 작성 아이디가 큰 해당 게시글 보다 큰 게시글들을 아이디 순으로 정렬한 다음 setMaxResults를 이용해 1개만 조회
		Query query = em.createQuery("select b from Board b where id>:boardId order by id");
		query.setParameter("boardId", boardId);
		query.setMaxResults(1);
		return (Board)query.getResultList().stream().findAny().orElse(null);
	}
}
