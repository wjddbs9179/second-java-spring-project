package hello.board.repository.shop.cart;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import hello.board.domain.Cart;
import hello.board.domain.Member;

public interface CartRepository extends JpaRepository<Cart, Long>{
	List<Cart> findAllByMember(Member member);
}
