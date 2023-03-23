package hello.board.controller.member;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
//비밀번호 찾기 기능에서 사용될 폼
@Data
public class PwSearchForm {
	@NotBlank
	private String userId;
	@NotBlank
	private String userName;
	@NotBlank
	private String email;
}
