package hello.board.repository.board.dislike;

import org.springframework.data.jpa.repository.JpaRepository;

import hello.board.domain.Dislike;

public interface DislikeRepository extends JpaRepository<Dislike, Long> {

}
