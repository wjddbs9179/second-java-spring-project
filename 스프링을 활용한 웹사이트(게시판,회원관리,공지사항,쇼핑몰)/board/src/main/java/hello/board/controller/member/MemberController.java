package hello.board.controller.member;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hello.board.domain.Member;
import hello.board.service.admin.AdminService;
import hello.board.service.member.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//자동의존관계 주입을 위해 @RequiredArgsConstructor 사용
@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final AdminService adminService;
	//회원가입
	@GetMapping("/member/join")
	public String join(Model model){
		//타임리프 th:object, th:field 기능을 사용하기 위해 새로운 Member객체를 model에 저장하고 회원가입 페이지로 이동
		model.addAttribute("member",new Member());
		return "member/join";
	}
	//회원가입
	@PostMapping("/member/join")
	public String join(@Validated @ModelAttribute("member") Member member,BindingResult br) {
		//회원가입 매서드 호출 아이디나 이메일이 중복일 경우 RuntimeException을 던짐
		try {
			if(member.getUserId().equals("system"))
				member.setAdmin(true);
			memberService.join(member);
		}catch(RuntimeException e) {
			log.info("e.getMessage={}",e.getMessage());
			//아이디가 중복일 경우
			if(e.getMessage().contains("USER_ID"))
				br.rejectValue("userId", null, "중복된 아이디 입니다.");
			//이메일이 중복일 경우
			if(e.getMessage().contains("EMAIL"))
				br.rejectValue("email", null,"중복된 이메일 입니다.");
		}
		//검증 실패시 다시 회원가입 페이지로 이동
		if(br.hasErrors()) 
			return "member/join";
		//회원가입 완료시
		return "member/join-success";
	}
	//메인 페이지(로그인, 공지사항)
	@GetMapping("/member/home")
	public String home(Model model, HttpSession session) {
		Long memberId = (Long)session.getAttribute("memberId");
		//공지사항 리스트(getHomeNoticeList는 공지사항을 최신순으로 5개만 조회함.) 저장
		model.addAttribute("noticeList", adminService.getHomeNoticeList());
		//현재 로그인 한 회원이 관리자인지 확인하고 관리자면 관리자 페이지로 이동
		if(session.getAttribute("isAdmin")!=null) {
			if((boolean)session.getAttribute("isAdmin")) {
				Member member = memberService.info(memberId);
				model.addAttribute("userName",member.getUserName());
				return "admin/home";
			}
		}
		//관리자가 아니거나 로그인이 안되어 있을 경우
		model.addAttribute("loginForm",new LoginForm());
		//로그인은 되어있는데 관리자가 아닐 경우
		if(memberId!=null) {
			Member member = memberService.info(memberId);
			model.addAttribute("userName",member.getUserName());
		}
		return "member/home";
	}
	//로그인
	@PostMapping("/member/login")
	public String login(Model model, @ModelAttribute("loginForm") LoginForm loginForm,HttpSession session,RedirectAttributes ra) {
		Member member = memberService.login(loginForm);
		//로그인 성공 했을경우 member는 null이 아님
		if(member!=null) {
			//세션에 memberId저장
			session.setAttribute("memberId", member.getId());
			//로그인한 사람이 판매자일 경우 true, 아닐 경우 false저장
			session.setAttribute("isSeller", member.isSeller());
			//로그인한 사람이 관리자일 경우 true, 아닐 경우 false저장
			session.setAttribute("isAdmin", member.isAdmin());
			//로그인 성공시 세션에 정보를 저장하고 /member/home요청
			return "redirect:/member/home";
		}
		//로그인 실패시
		return "member/loginFail";
	}
	//로그아웃
	@GetMapping("/member/logout")
	public String logout(HttpSession session) {
		//세션에 저장된 정보를 제거하고 /member/home으로 이동
		session.invalidate();
		return "redirect:/member/home";
	}
	//아이디 비밀번호 찾기
	@GetMapping("/member/idpwsearch")
	public String idpwSearch() {
		//아이디 찾기/비밀번호 찾기를 선택할 수 있는 페이지로 이동
		return "member/idpw-search";
	}
	//아이디 찾기
	@GetMapping("/member/idSearch")
	public String idSearch(Model model) {
		model.addAttribute("form",new IdSearchForm());
		return "member/idsearch";
	}
	//아이디 찾기
	@PostMapping("/member/idSearch")
	public String idSearch(@Validated @ModelAttribute IdSearchForm form,BindingResult br, Model model) {
		Member member = memberService.idSearch(form);
		//파라미터로 빈문자열이 넘어왔는지 검증
		if(br.hasErrors())
			return "member/idpw-search-fail";
		//memberService.idSearch로 반환된 객체가 있으면 인증번호 6자리를 생성하여 다음페이지로 넘겨줌
		if(member!=null) {
			model.addAttribute("cNum", (int)(Math.random()*899999)+100000);
			model.addAttribute("member",member);
			return "member/idsearch-success";
		}else {
			//조회된 member객체가 없을 시 이동할 페이지
			return "member/idpw-search-fail";
		}
	}
	//아이디 찾기에서 인증번호를 맞게 입력했는지 확인
	@PostMapping("/member/cNumCheck_ID")
	public String cNumCheck_id(Model model, 
			@RequestParam String cNumCheck, 
			@RequestParam String cNum, 
			@RequestParam String userId) {
		//인증번호가 맞으면 model에 회원의 아이디를 보여주기 위해 model에 아이디 저장
		//인증번호가 맞지 않으면 사용자에게 메세지를 보여주기 위한 페이지로 이동
		if(cNumCheck.equals(cNum)) {
			model.addAttribute("userId",userId);
		}else {
			return "member/cNumCheck-fail";
		}
		//인증번호가 맞으면 model에 저장된 userId를 출력하기 위한 페이지
		return "member/idSearch-com";
	}
	//비밀번호 찾기
	@GetMapping("/member/pwSearch")
	public String pwSearch(Model model) {
		model.addAttribute("form",new PwSearchForm());
		return"member/pwsearch";
	}
	//비밀번호 찾기
	@PostMapping("/member/pwSearch")
	public String pwSearch(Model model, @Validated PwSearchForm form,BindingResult br) {
		//빈 문자열이 넘어왔는지 검증
		if(br.hasErrors())
			return"member/idpw-search-fail";
		//비밀번호 찾기 메서드 PwSearchForm 객체를 넘겨주고 조회한 객체가 있을 시 member 반환
		Member member = memberService.pwSearch(form);
		//member가 null이면 인증번호를 생성하여 model에 저장
		if(member!=null) {
			model.addAttribute("memberId",member.getId());
			model.addAttribute("cNum",(int)(Math.random()*899999)+100000);
			model.addAttribute("member",member);
			return"member/pwsearch-success";
		}else {
			//member가 null이면 해당하는 정보를 가진 회원이 없다는 말 이므로 fail페이지로 이동
			return "member/idpw-search-fail";
		}
	}
	//비밀번호 찾기
	@PostMapping("/member/cNumCheck_PW")
	public String cNumCheck_pw(Model model,
			@RequestParam String cNumCheck,
			@RequestParam String cNum,
			@RequestParam Long memberId) {
		//인증번호를 맞게 입력 했을 시 임시비밀번호 발급
		if(cNumCheck.equals(cNum)) {
			String exPw = "";
			String[] exPwArr = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
					,"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
					"1","2","3","4","5","6","7","8","9","0","~","!","@","#","$"};
			//임시 비밀번호는 영문,숫자,특수문자 조합 13자리
			for(int i=0;i<13;i++) {
				exPw+=exPwArr[(int)(Math.random()*exPwArr.length)];
			}
			//실제 회원의 비밀번호를 임시비밀번호로 변경하는 매서드 호출
			String password = memberService.extraPwUpdate(memberId,exPw);
			//회원에게 변경된 임시비밀번호를 확인시켜주기 위해 model에 비밀번호 저장
			model.addAttribute("password",password);
		}else {
			//인증번호를 틀렸을 경우 보여줄 페이지
			return "member/cNumCheck-fail";
		}
		//인증번호를 맞게 입력했을 시 보여줄 페이지
		return "member/pwSearch-com";
	}
	//내 정보
	@GetMapping("/member/info")
	public String info(HttpSession session, Model model) {
		//세션에서 memberId를 가져와서 회원을 조회 하고 조회된 member객체를 model에 저장
		Long memberId = (Long)session.getAttribute("memberId");
		Member member = memberService.info(memberId);
		model.addAttribute("member",member);
		return "member/info";
	}
	//회원정보 수정
	@GetMapping("/member/update/{id}")
	public String update(Model model,@PathVariable Long id,HttpSession session) {
		//로그인 하지 않은 회원의 비정상적인 요청일 경우
		if(session.getAttribute("memberId")==null)
			return "board/login-check";
		//id로 회원정보를 조회하여 MemberUpdateForm객체에 세팅하고 model에 저장 후 회원정보 수정 페이지로 이동
		Member member = memberService.info(id);
		MemberUpdateForm form = new MemberUpdateForm();
		form.setEmail(member.getEmail());
		form.setUserName(member.getUserName());
		form.setPassword(member.getPassword());
		model.addAttribute("form",form);
		return "member/update";
	}
	//회원정보 수정
	@PostMapping("/member/update/{id}")
	public String update(@PathVariable Long id,
			@ModelAttribute("form") @Validated MemberUpdateForm form,BindingResult br,
			HttpSession session) {
		//로그인 하지 않은 회원의 비정상적인 요청일 경우
		if(session.getAttribute("memberId")==null)
			return "board/login-check";

		//회원정보에 빈 문자열이 들어왔는지 검증
		if(br.hasErrors())
			return "member/update";
		//검증 완료후 데이터베이스에 업데이트 하는 매서드 호출 
		//form객체와 memberId를 넘겨주면 memberId로 회원을 조회하고 form 객체에 저장되어 있는 값을 이용하여 정보를 수정하고 데이터베이스에 저장된 값을 업데이트 한다.
		memberService.update(form,(Long)session.getAttribute("memberId"));
		return "redirect:/member/info";
	}
	//회원정보 수정을 위한 비밀번호 확인
	@GetMapping("/member/update/check")
	public String update_check() {
		return "member/update-password-check-form";
	}
	//회원정보 수정을 위한 비밀번호 확인
	@PostMapping("/member/update/check")
	public String update_check(@RequestParam String password,HttpSession session) {
		//세션에 저장된 memberId를 이용하여 member를 조회 하고 파라미터로 넘어온 비밀번호와 일치하는 지 확인
		Long memberId = (Long)session.getAttribute("memberId");
		Member member = memberService.info(memberId);
		//비밀번호가 맞으면 update url로 리다이렉트 틀렸을 경우 실패메세지 출력 페이지로 이동
		if(password.equals(member.getPassword())) {
			return "redirect:/member/update/"+memberId;
		}else {
			return "member/update-password-check-fail";
		}
	}
	//회원탈퇴
	@GetMapping("/member/drop")
	public String drop() {
		return "member/drop-password-check";
	}
	//회원탈퇴 시 비밀번호를 입력받도록 구현
	@PostMapping("/member/drop")
	public String dropPwCheck(@RequestParam String checkPw, HttpSession session) {
		//세션에 있는 memberId로 member객체를 조회하고 파라미터로 넘어온 비밀번호와 일치하는지 확인
		Long memberId = (Long)session.getAttribute("memberId");
		Member member = memberService.info(memberId);
		//비밀번호가 일치할 경우 데이터베이스에 저장된 정보를 삭제하고 세션에 저장된 정보를 삭제
		if(checkPw.equals(member.getPassword())) {
			memberService.drop(member.getId());
			session.invalidate();
		}else {
			return "member/drop-fail";
		}
		//성공했을 경우 삭제되었다는 메세지를 출력하는 페이지로 이동 
		return "member/drop-success";
	}
}
