package hello.board.domain;

import java.util.Date;

import org.hibernate.validator.constraints.NotBlank;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

//게시판 댓글 entity
@Entity
@Data
public class Comment {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String userId;
	private Long boardId;
	@NotBlank
	private String content;
	private Date inputDate;
}
