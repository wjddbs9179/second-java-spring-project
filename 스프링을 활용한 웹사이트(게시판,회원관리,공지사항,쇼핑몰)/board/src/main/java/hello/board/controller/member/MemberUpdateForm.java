package hello.board.controller.member;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

//회원정보 수정기능에서 사용될 폼
@Data
public class MemberUpdateForm {
	@NotBlank
	private String userName;
	@NotBlank
	private String email;
	@NotBlank
	private String password;
}
