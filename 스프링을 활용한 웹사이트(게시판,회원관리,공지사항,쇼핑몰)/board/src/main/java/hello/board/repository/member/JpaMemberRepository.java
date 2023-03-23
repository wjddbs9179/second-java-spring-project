package hello.board.repository.member;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import hello.board.controller.member.IdSearchForm;
import hello.board.controller.member.LoginForm;
import hello.board.controller.member.PwSearchForm;
import hello.board.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
//회원관리를 위한 JPARepository
@Repository
@RequiredArgsConstructor
public class JpaMemberRepository {
	private final EntityManager em;
	//로그인 기능을 구현하기 위해 loginForm을 매개변수로 받아와서 emptyTest매서드를 이용해 아이디와 비밀번호가 일치하는 회원이 있는지 확인하고 있으면 해당 Member객체를 반환
	public Member findByLoginForm(LoginForm loginForm) {
		Query query = em.createQuery("select m from Member m where userId=:userId and password=:password");
		query.setParameter("userId", loginForm.getUserId());
		query.setParameter("password", loginForm.getPassword());
		return emptyTest(query);
	}
	//아이디 찾기 기능을 구현하기 위해 만든 매서드 작동 방식은 로그인 findByLoginForm과 비슷함
	public Member findByIdSearchForm(IdSearchForm form) {
		Query query = em.createQuery("select m from Member m where userName=:userName and email=:email");
		query.setParameter("userName", form.getUserName());
		query.setParameter("email", form.getEmail());
		return emptyTest(query);
	}
	//비밀번호 찾기 기능을 구현하기 위해 만든 매서드 위에 다른 매서드와 작동방식은 비슷함
	public Member findByPwSearchForm(PwSearchForm form) {
		Query query = em.createQuery("select m from Member m where userName=:userName and email=:email and userId=:userId");
		query.setParameter("userName", form.getUserName());
		query.setParameter("email", form.getEmail());
		query.setParameter("userId", form.getUserId());
		return emptyTest(query);
	}
	//조회된 회원객체가 있는지 검증하는 매서드 조회된 객체없으면 null을 반환
	private Member emptyTest(Query query) {
		Optional<Member> opMember = query.getResultList().stream().findAny();
		if(opMember.isEmpty())
			return null;
		return opMember.get();
	}
	public List<Member> findBySeller() {
		Query query = em.createQuery("select m from Member m where isSeller=true");
		return query.getResultList();
	}
}
