package hello.board.repository.shop.order;

import java.util.List;

import org.springframework.stereotype.Repository;

import hello.board.domain.Orders;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

//상품 주문기능을 위한 JPA Repository 쇼핑몰 기능은 아직 구현되지 않았음
@Repository
@RequiredArgsConstructor
public class JpaOrderRepository {
	
	private final EntityManager em;
	
	public List<Orders> findByMemberId(Long memberId) {
		Query query = em.createQuery("select o from Orders o where memberId=:memberId");
		query.setParameter("memberId", memberId);
		return query.getResultList();
	}

	public List<Orders> findBySellerId(Long sellerId) {
		Query query = em.createQuery("select o from Orders o where sellerId=:sellerId");
		query.setParameter("sellerId", sellerId);
		return query.getResultList();
	}

}
