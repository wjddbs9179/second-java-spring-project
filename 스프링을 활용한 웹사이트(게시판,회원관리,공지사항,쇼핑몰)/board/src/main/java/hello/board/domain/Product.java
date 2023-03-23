package hello.board.domain;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

//상품 entity
@Data
@Entity
public class Product {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank
	private String name;
	//최소가격 1000원, 최대가격1000만원
	@NotNull
	@Min(value = 1000) @Max(value = 10000000)
	private int price;
	//최소수량 10, 최대수량 9999
	@NotNull
	@Min(value = 0) @Max(value = 9999)
	private int quantity;
	private String imagePath;
	private Long memberId;
	private Category category;
	@OneToMany(mappedBy = "product")
	List<Cart> carts = new ArrayList<>();
	
}
