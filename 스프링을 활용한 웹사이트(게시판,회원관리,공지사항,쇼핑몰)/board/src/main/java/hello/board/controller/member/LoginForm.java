package hello.board.controller.member;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
//로그인기능을 구현하기 위한 폼
@Data
public class LoginForm {
	@NotBlank
	private String userId;
	@NotBlank
	private String password;
}
