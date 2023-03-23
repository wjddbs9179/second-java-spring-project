package hello.board.repository.shop.order;

import org.springframework.data.jpa.repository.JpaRepository;

import hello.board.domain.Orders;
//상품 주문을 위한 Spring Data JPA 인터페이스
public interface OrderRepository extends JpaRepository<Orders, Long>{

}
