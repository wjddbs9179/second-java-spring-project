package hello.board.domain;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;


//회원객체 entity
@Data
@Entity
public class Member {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank
	@Column(unique = true)
	private String userId;
	@NotBlank
	private String userName;
	@NotBlank
	private String password;
	@NotBlank
	@Column(unique = true)
	private String email;
	private boolean isAdmin;
	private boolean isSeller;
	//Member Entity
	@OneToMany(mappedBy = "member")
	private List<Likes> likes = new ArrayList<>();
	@OneToMany(mappedBy = "member")
	private List<Dislike> dislikes = new ArrayList<>();
	@OneToMany(mappedBy = "member")
	private List<Cart> cart = new ArrayList<>();
	private String phone;
	private String address;
}
