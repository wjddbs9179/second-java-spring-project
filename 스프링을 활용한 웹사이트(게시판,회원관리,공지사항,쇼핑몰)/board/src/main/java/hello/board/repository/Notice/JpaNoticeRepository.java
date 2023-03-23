package hello.board.repository.Notice;

import java.util.List;

import org.springframework.stereotype.Repository;

import hello.board.domain.Notice;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
//공지사항 기능을 위한 JPARepository
@Repository
@RequiredArgsConstructor
public class JpaNoticeRepository {
	//RequiredArgsConstructor을 이용해서 자동 의존관계 주입
	private final EntityManager em;
	
	//메인화면에 출력할 공지사항 리스트 5개만 조회되도록 구현하였음.
	@SuppressWarnings("unchecked")
	public List<Notice> findAll(){
		Query query = em.createQuery("select n from Notice n order by id desc");
		query.setMaxResults(5);
		return query.getResultList();
	}
	//공지사항 전체목록을 가져오는 매서드 메인화면에서 더보기를 눌렸을 때 전체 리스트를 보여주기 위해 구현하였음.
	@SuppressWarnings("unchecked")
	public List<Notice> findAllList() {
		Query query = em.createQuery("select n from Notice n order by id desc");
		return query.getResultList();
	}
}
