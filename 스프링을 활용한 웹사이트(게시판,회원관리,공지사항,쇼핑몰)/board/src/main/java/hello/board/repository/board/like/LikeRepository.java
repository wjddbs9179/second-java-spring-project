package hello.board.repository.board.like;

import org.springframework.data.jpa.repository.JpaRepository;

import hello.board.domain.Likes;

public interface LikeRepository extends JpaRepository<Likes, Long>{

}
