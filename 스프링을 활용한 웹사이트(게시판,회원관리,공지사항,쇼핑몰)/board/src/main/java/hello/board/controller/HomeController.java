package hello.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//홈 컨트롤러로서 url을 입력안했을 시 로그인을 할 수 있는 메인 페이지로 이동
@Controller
public class HomeController {
	@GetMapping("/")
	public String home() {
		return "redirect:/member/home";
	}
}
