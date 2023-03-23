package hello.board.repository.shop.cart;

import java.util.List;

import org.springframework.stereotype.Repository;

import hello.board.domain.Cart;
import hello.board.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaCartRepository {
	private final EntityManager em;
	
	public List<Cart> findByMember(Member member) {
		Query query = em.createQuery("select c from Cart c where member=:member");
		query.setParameter("member", member);
		return query.getResultList();
	}

}
