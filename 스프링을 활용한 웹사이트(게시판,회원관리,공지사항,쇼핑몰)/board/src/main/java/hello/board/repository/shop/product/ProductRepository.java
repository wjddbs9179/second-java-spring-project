package hello.board.repository.shop.product;

import org.springframework.data.jpa.repository.JpaRepository;

import hello.board.domain.Product;
//상품 등록,수정,삭제,조회를 위한 Spring Data JPA 인터페이스
public interface ProductRepository extends JpaRepository<Product, Long>{
	
}
