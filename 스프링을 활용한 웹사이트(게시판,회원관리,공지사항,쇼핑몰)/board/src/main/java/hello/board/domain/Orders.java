package hello.board.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
//주문 entity 아직 쇼핑몰 기능 미완성
@Data
@Entity
public class Orders {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long sellerId;
	private Long memberId;
	private Long productId;
	private String productName;
	private int quantity;
	private int totalPrice;
	private OrderStatus status;
	private String address;
	private String phone;
}
