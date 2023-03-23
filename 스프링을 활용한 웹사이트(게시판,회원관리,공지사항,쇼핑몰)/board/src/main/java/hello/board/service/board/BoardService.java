package hello.board.service.board;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import hello.board.domain.Board;
import hello.board.domain.Comment;
import hello.board.domain.Dislike;
import hello.board.domain.Likes;
import hello.board.domain.Member;
import hello.board.repository.board.BoardRepository;
import hello.board.repository.board.JpaBoardRepository;
import hello.board.repository.board.comment.CommentRepository;
import hello.board.repository.board.comment.JpaCommentRepository;
import hello.board.repository.board.dislike.DislikeRepository;
import hello.board.repository.board.dislike.JpaDislikeRepository;
import hello.board.repository.board.like.JpaLikeRepository;
import hello.board.repository.board.like.LikeRepository;
import hello.board.repository.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
//게시판 기능의 서비스계층
@Service
@RequiredArgsConstructor
public class BoardService {
	
	//@RequiredArgsConstructor을 이용한 자동의존관계 주입
	private final BoardRepository boardRepository;
	private final JpaBoardRepository jpaBoardRepository;
	private final CommentRepository commentRepository;
	private final JpaCommentRepository jpaCommentRepository;
	private final MemberRepository memberRepository;
	private final LikeRepository likeRepository;
	private final DislikeRepository dislikeRepository;
	private final JpaLikeRepository jpaLikeRepository;
	private final JpaDislikeRepository jpaDislikeRepository;
	
	//게시글 등록
	public Board reg(Board board) {
		return boardRepository.save(board);
	}
	//게시글 조회
	public Board get(Long id) {
		return boardRepository.findById(id).orElse(null);
	}
	//게시글 수정
	@Transactional
	public void update(Board board) {
		Board findBoard = boardRepository.findById(board.getId()).orElse(null);
		findBoard.setContent(board.getContent());
	}
	//게시글 삭제
	public void delete(Long id) {
		boardRepository.deleteById(id);
	}
	//게시글 목록 페이징처리를 위한 매개변수 처리
	public List<Board> getList(int page,String field,String query,int maxResult, String sort){
		return jpaBoardRepository.findAllPage(page,field,query,maxResult,sort);
	}
	//검색어에 따른 게시글 수 조회 페이징처리를 위해 필요함.
	public long getCount(String field, String query) {
		return jpaBoardRepository.count(field,query);
	}
	//게시글 조회시 조회수+1
	@Transactional
	public void readCountUp(Long id) {
		Board board = boardRepository.findById(id).orElse(null);
		board.setReadCount(board.getReadCount()+1);
	}
	//댓글작성
	public void commentReg(Comment comment,String userId,Long boardId) {
		comment.setUserId(userId);
		comment.setBoardId(boardId);
		comment.setInputDate(new Date());
		commentRepository.save(comment);
	}
	//다음글 기능
	public Board nextBoard(Long boardId) {
		return jpaBoardRepository.findNextBoard(boardId);
	}
	//이전글 기능
	public Board prevBoard(Long boardId) {
		return jpaBoardRepository.findPrevBoard(boardId);
	}
	//해당 게시글의 댓글리스트
	public List<Comment> getComments(Long id) {
		return jpaCommentRepository.findByBoardId(id);
	}
	//댓글 삭제
	public void commentDelete(Long commentId) {
		commentRepository.deleteById(commentId);
	}
	@Transactional
	public void likesUp(Long boardId,Long memberId) {
		Long id = jpaLikeRepository.findIdByMemberAndBoard(memberId, boardId);
		if(id!=null)
			return;
		Long dislikeId = jpaDislikeRepository.findIdByMemberAndBoard(memberId, boardId);
		if(dislikeId!=null)
			dislikeRepository.deleteById(dislikeId);
		Likes like = new Likes();
		like.setMember(memberRepository.findById(memberId).orElse(null));
		like.setBoard(boardRepository.findById(boardId).orElse(null));
		likeRepository.save(like);
	}
	@Transactional
	public void dislikesUp(Long boardId,Long memberId) {
		Long id = jpaDislikeRepository.findIdByMemberAndBoard(memberId, boardId);
		if(id!=null)
			return;
		Long likeId = jpaLikeRepository.findIdByMemberAndBoard(memberId, boardId);
		if(likeId!=null)
			likeRepository.deleteById(likeId);
		Dislike dislike = new Dislike();
		dislike.setMember(memberRepository.findById(memberId).orElse(null));
		dislike.setBoard(boardRepository.findById(boardId).orElse(null));
		dislikeRepository.save(dislike);
	}
	@Transactional
	public void likesDown(Long boardId, Long memberId) {
		Long id = jpaLikeRepository.findIdByMemberAndBoard(memberId, boardId);
		if(id==null)
			return;
		likeRepository.deleteById(id);
	}
	@Transactional
	public void dislikesDown(Long boardId, Long memberId) {
		Long id = jpaDislikeRepository.findIdByMemberAndBoard(memberId,boardId);
		if(id==null)
			return;
		dislikeRepository.deleteById(id);
	}
}
