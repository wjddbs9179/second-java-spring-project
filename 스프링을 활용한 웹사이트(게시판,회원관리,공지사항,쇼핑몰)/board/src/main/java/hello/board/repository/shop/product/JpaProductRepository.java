package hello.board.repository.shop.product;

import java.util.List;

import org.springframework.stereotype.Repository;

import hello.board.domain.Category;
import hello.board.domain.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;


//상품등록,수정,삭제,조회를 위한 JPA Repository
@Repository
@RequiredArgsConstructor
public class JpaProductRepository {
	private final EntityManager em;

	public List<Product> findByPage(int page, int maxResult,String category) {
		Query query = em.createQuery("select p from Product p where category=:category order by id desc");
		if(category.equals("BOOK"))
			query.setParameter("category", Category.BOOK);
		if(category.equals("CLOTH"))
			query.setParameter("category", Category.CLOTH);
		if(category.equals("FOOD"))
			query.setParameter("category", Category.FOOD);
		
		query.setFirstResult((page-1)*maxResult);
		query.setMaxResults(maxResult);
		return query.getResultList();
	}

	public Long getCount(String category) {
		Query query = em.createQuery("select count(*) from Product p where category=:category");
		if(category.equals("BOOK"))
			query.setParameter("category", Category.BOOK);
		if(category.equals("CLOTH"))
			query.setParameter("category", Category.CLOTH);
		if(category.equals("FOOD"))
			query.setParameter("category", Category.FOOD);
		query.setMaxResults(1);
		return (Long)query.getResultList().stream().findAny().orElse(null);
	}
}
