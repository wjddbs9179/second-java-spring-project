package hello.board.controller.member;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
//아이디 찾기 기능을 구현하기 위해 만든 폼
@Data
public class IdSearchForm {
	//NotBlank는 빈문자열이 파라미터로 넘어왔을 시 bindingResult로 검증 가능
	@NotBlank
	private String userName;
	@NotBlank
	private String email;
}
