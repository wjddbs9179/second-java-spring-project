package hello.board.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

//게시판 게시글 entity
@Data
@Entity
public class Board {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank(message = "제목을 작성해주세요.")
	private String subject;
	@NotBlank(message = "내용을 작성해주세요.")
	private String content;
	@NotBlank(message = "작성자를 입력해주세요.")
	private String writer;
	@Temporal(TemporalType.DATE)
	private Date inputDate;
	private long readCount;
	private String password;
	@OneToMany(mappedBy = "board")
	private List<Likes> likes = new ArrayList<>();
	@OneToMany(mappedBy = "board")
	private List<Dislike> dislikes = new ArrayList<>();
}
