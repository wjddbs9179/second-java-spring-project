package hello.board.domain;

import java.util.Date;

import org.hibernate.validator.constraints.NotBlank;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

//관리자 답글 entity
@Data
@Entity
public class AdminComment {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long boardId;
	private String userId;
	@NotBlank
	private String subject;
	@NotBlank
	private String content;
	private Date inputDate;
	
}
