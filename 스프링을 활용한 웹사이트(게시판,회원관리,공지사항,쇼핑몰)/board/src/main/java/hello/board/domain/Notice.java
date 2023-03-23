package hello.board.domain;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

//공지사항 entity
@Data
@Entity
public class Notice {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String subject;
	private String content;
	private Date inputDate;
	private int readCount;
}
