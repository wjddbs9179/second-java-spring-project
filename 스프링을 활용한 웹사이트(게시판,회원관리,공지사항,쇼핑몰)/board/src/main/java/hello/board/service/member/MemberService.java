package hello.board.service.member;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;

import hello.board.controller.member.IdSearchForm;
import hello.board.controller.member.LoginForm;
import hello.board.controller.member.MemberUpdateForm;
import hello.board.controller.member.PwSearchForm;
import hello.board.domain.Member;
import hello.board.repository.member.JpaMemberRepository;
import hello.board.repository.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
//회원관리의 서비스 계층
@Service
@RequiredArgsConstructor
public class MemberService {
	//@RequiredArgsConstructor을 이용한 자동 의존관계 주입
	private final JpaMemberRepository jpaMemberRepository;
	private final MemberRepository memberRepository;
	//회원가입
	@Transactional
	public Member join(Member member) {
		//회원가입 검증 로직, 검증 실패시 RuntimeException을 던지고 Controller에서 잡아서 처리, 아이디 중복 또는 이메일 중복시 여기에 해당함
		try {
			memberRepository.save(member);
		}catch(ConstraintViolationException e) {
			throw new RuntimeException(e);
		}
		//회원가입이 정상적으로 완료 되었을 경우 member객체 반환
		return member;
	}
	//로그인 기능 loginForm을 매개변수로 해서 Repository에 위임
	public Member login(LoginForm loginForm) {
		return jpaMemberRepository.findByLoginForm(loginForm);
	}
	//아이디 찾기
	public Member idSearch(IdSearchForm form) {
		return jpaMemberRepository.findByIdSearchForm(form);
	}
	//비밀번호 찾기
	public Member pwSearch(PwSearchForm form) {
		return jpaMemberRepository.findByPwSearchForm(form);
	}
	//회원정보 조회
	public Member info(Long id) {
		return memberRepository.findById(id).orElse(null);
	}
	//회원정보 수정
	@Transactional
	public void update(MemberUpdateForm form, Long id) {
		//매개변수로 넘어온 id를 이용해서 member객체를 조회하고 수정함.
		//@Transactinal을 붙여놓으면 jpa가 객체의 필드값 변경시 변경을 감지하여 자동으로 db에 업데이트 쿼리문을 수행함
		Member member = memberRepository.findById(id).orElse(null);
		if(member!=null) {
			member.setEmail(form.getEmail());
			member.setUserName(form.getUserName());
			member.setPassword(form.getPassword());
		}
	}
	//비밀번호 찾기 시 임시비밀번호 발급
	@Transactional
	public String extraPwUpdate(Long memberId, String exPw) {
		Member member = memberRepository.findById(memberId).orElse(null);
		member.setPassword(exPw);
		return member.getPassword();
	}
	//회원탈퇴
	public void drop(Long id) {
		memberRepository.deleteById(id);
	}
	public List<Member> sellerList() {
		return jpaMemberRepository.findBySeller();
	}
	@Transactional
	public void sellerCancel(Long memberId) {
		Member member = memberRepository.findById(memberId).orElse(null);
		member.setAddress(null);
		member.setPhone(null);
		member.setSeller(false);
	}
}
