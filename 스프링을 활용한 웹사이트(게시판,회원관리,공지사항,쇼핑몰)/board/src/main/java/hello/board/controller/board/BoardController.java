package hello.board.controller.board;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hello.board.domain.Board;
import hello.board.domain.Comment;
import hello.board.domain.Dislike;
import hello.board.domain.Likes;
import hello.board.domain.Member;
import hello.board.service.admin.AdminService;
import hello.board.service.board.BoardService;
import hello.board.service.member.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//의존관계 자동 주입을 위해 RequiredArgsConstructor 사용
//url매핑의 공통된 부분을 RequestMapping으로 사용
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

	//의존관계
	private final MemberService memberService;
	private final BoardService boardService;
	private final AdminService adminService;

	//페이징 세션 초기화
	@GetMapping("/paging/session/reset")
	public String session_reset(HttpSession session) {
		session.setAttribute("maxResult", 10);
		session.setAttribute("page", 1);
		session.setAttribute("field", "subject");
		session.setAttribute("query", "");
		session.setAttribute("sort", "id");
		return "redirect:/board/list";
	}

	//게시판 메인 페이지
	@GetMapping("/list")
	public String boardList(Model model,
			@RequestParam(defaultValue = "10") int maxResult,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "subject") String field,
			@RequestParam(defaultValue = "") String query,
			@RequestParam(defaultValue = "id")String sort,HttpSession session) {

		if(maxResult==10&&field.equals("subject")&&query.equals("")&&sort.equals("id")) {
			if(session.getAttribute("maxResult")!=null)
				maxResult=(int)session.getAttribute("maxResult");
			if(session.getAttribute("field")!=null)
				field= (String)session.getAttribute("field");
			if(session.getAttribute("query")!=null)
				query = (String)session.getAttribute("query");
			if(session.getAttribute("sort")!=null)
				sort = (String)session.getAttribute("sort");
		}
		if(session.getAttribute("likeOrDislikePage")!=null) {
			page=(int)session.getAttribute("likeOrDislikePage");
			session.setAttribute("likeOrDislikePage", null);
		}

		if(sort.equals("최신 순"))
			sort = "id";
		if(sort.equals("조회수 순"))
			sort = "readCount";
		if(sort.equals("좋아요 순"))
			sort = "likes";
		//검색기능을 구현하기 위한 로직 select박스로 넘어오는 값은 제목,작성자,내용처럼 한글로 넘어오기 때문에 jpql을 작성할 때 바로 사용할 수 없어서 컬럼명으로 변경해 주었음.
		if(field.equals("제목"))
			field="subject";
		if(field.equals("작성자"))
			field="writer";
		if(field.equals("내용"))
			field="content";
		//여기서 부터는 두가지 이상 필드가 합쳐진 형태이기 때문에 jpql을 따로 작성해서 상황에 맞는 jpql이 실행 되도록 구현 함.
		if(field.equals("제목+내용"))
			field="subjectAndContent";
		if(field.equals("제목+작성자"))
			field="subjectAndWriter";
		if(field.equals("내용+작성자"))
			field="contentAndWriter";
		if(field.equals("제목+내용+작성자"))
			field="subjectAndContentAndWriter";
		//page는 현재 페이지, field는 검색조건, query는 검색 키워드, maxResult는 한페이지에 출력할 게시글 수.
		model.addAttribute("list",boardService.getList(page,field,query,maxResult,sort));
		//페이징 처리를 위해 필요한 변수들을 계산하는 로직
		long count = boardService.getCount(field,query);
		long totalPage = count%maxResult==0?count/maxResult:count/maxResult+1;
		if(count==0)
			totalPage=1;
		long firstPage = page%5==0?page-4:page/5*5+1;
		long lastPage = firstPage+4;
		//lastPage가 totalPage보다 클 경우 예를 들어 13페이지 까지 밖에 없는데 15페이지 까지 선택할 수 있게 출력되기 때문에 이 경우에는 lastPage는 totalPage로 변경
		if(lastPage>totalPage) 
			lastPage=totalPage;
		//페이지를 출력하기 위한 변수들 model에 저장, 관리자 답글도 포함되어있음.
		model.addAttribute("sort",sort);
		model.addAttribute("adminComments",adminService.adminComments());
		model.addAttribute("maxResult",maxResult);
		model.addAttribute("field",field);
		model.addAttribute("query",query);
		model.addAttribute("page",page);
		model.addAttribute("count",count);
		model.addAttribute("totalPage",totalPage);
		model.addAttribute("firstPage",firstPage);
		model.addAttribute("lastPage",lastPage);

		session.setAttribute("sort", sort);
		session.setAttribute("maxResult", maxResult);
		session.setAttribute("field", field);
		session.setAttribute("query", query);
		session.setAttribute("page", page);
		return "board/list";
	}
	//게시글 등록을 위한 url
	@GetMapping("reg")
	public String reg(Model model,HttpSession session) {
		//사용자가 로그인을 했는지 확인하고 로그인을 하지 않았다면 login-check(alert창) -> /member/home으로 리다이렉트 시킴.
		if(session.getAttribute("memberId")==null)
			return "board/login-check";
		//게시글의 작성자를 현재 로그인한 회원의 아이디로 세팅하고 새로운 board객체를 다음 페이지로 넘겨줌
		Long memberId = (Long)session.getAttribute("memberId");
		Member member = memberService.info(memberId);

		Board board = new Board();
		board.setWriter(member.getUserId());
		model.addAttribute("board",board);
		return "board/reg-form";
	}
	//게시글 등록을 위한 url
	@PostMapping("reg")
	public String reg(@Validated @ModelAttribute Board board, BindingResult br,HttpSession session) {
		//넘어온 board객체를 검증. error가 있으면 다시 게시글 등록 페이지로 이동
		if(br.hasErrors())
			return "board/reg-form";
		//로그인 한 사용자 인지 확인하고 로그인 하지 않았다면 login-check(alert창) -> /member/home으로 리다이렉트
		if(session.getAttribute("memberId")==null)
			return "board/login-check";
		//검증을 끝낸 후 게시글의 등록날짜를 세팅하고 실제 데이터베이스에 저장하는 매서드 호출
		board.setInputDate(new Date());
		boardService.reg(board);
		return "redirect:/board/list";
	}
	//게시글을 조회하는 url
	@GetMapping("board/{id}")
	public String board(@PathVariable Long id, Model model,
			@RequestParam(defaultValue = "1") String page,
			@RequestParam(defaultValue = "10") int maxResult,
			@RequestParam(defaultValue = "subject") String field,
			@RequestParam(defaultValue = "") String query,
			@RequestParam(defaultValue = "id") String sort,
			HttpSession session) {
		//게시글 조회시 조회수 증가

		if(session.getAttribute("likeOrDislike")!=null)
			if(!(boolean)session.getAttribute("likeOrDislike"))
				boardService.readCountUp(id);
		session.setAttribute("likeOrDislike", false);
		//게시글을 조회한 후 다시 목록으로 이동했을 때 기존에 보고있던 목록페이지로 이동하기 위해 필요한 변수들 model에 저장
		Board board = boardService.get(id);
		Member member = null;
		Likes like = null;
		Dislike dislike = null;
		if(session.getAttribute("memberId")!=null) {
			member = memberService.info((Long)session.getAttribute("memberId"));
			like = member.getLikes().stream().filter(likes->likes.getBoard().equals(board)).findAny().orElse(null);
			dislike = member.getDislikes().stream().filter(dislikes->dislikes.getBoard().equals(board)).findAny().orElse(null);
			model.addAttribute("userId",member.getUserId());
		}
		if(like!=null)
			model.addAttribute("isLike",true);
		else 
			model.addAttribute("isLike",false);
		if(dislike!=null)
			model.addAttribute("isDislike",true);
		else
			model.addAttribute("isDislike",false);

		model.addAttribute("sort",sort);
		model.addAttribute("page",page);
		model.addAttribute("maxResult",maxResult);
		model.addAttribute("field",field);
		model.addAttribute("query",query);
		//이전글, 다음글 기능을 구현
		model.addAttribute("nextBoard",boardService.nextBoard(id));
		model.addAttribute("prevBoard",boardService.prevBoard(id));
		//댓글작성을 위한 comment객체를 저장
		model.addAttribute("comment",new Comment());
		//게시글에 달려있는 댓글들을 model에 저장
		model.addAttribute("comments",boardService.getComments(id));
		//게시글 객체 model에 저장
		model.addAttribute("board", board);
		return "board/board";
	}
	//게시글을 수정하기 위한 url
	@GetMapping("update/{id}")
	public String update(@PathVariable Long id, Model model,HttpSession session) {
		//사용자 로그인을 확인하는 로직 로그인하지 않았으면 login-check -> /member/home으로 리다이렉트
		if(session.getAttribute("memberId")==null)
			return "board/login-check";
		//다른 사람의 글을 수정할 수 없게 자기자신의 글인지 확인 하는 로직 자기자신의 글이 아니면 my-board-check페이지로 이동하고 history.back();이 실행됨
		if(myBoardCheck(id, session))
			return "board/my-board-check";
		//검증을 마치면 게시글을 수정할 수 있는 페이지로 이동
		model.addAttribute("board",boardService.get(id));
		return "board/update-form";
	}
	//게시글을 수정하기 위한 포스트 url
	@PostMapping("update/{id}")
	public String update(Model model, @PathVariable Long id, @Validated @ModelAttribute Board board, BindingResult br, @RequestParam String checkPw,HttpSession session) {
		//로그인 검증
		if(session.getAttribute("memberId")==null)
			return "board/login-check";

		Member member = memberService.info((Long)session.getAttribute("memberId"));
		//자기 자신의 글인지 검증
		if(!member.getUserId().equals(board.getWriter()))
			return "board/my-board-check";
		//게시글의 비밀번호 검증
		if(!checkPw.equals(boardService.get(id).getPassword()))
			return "board/update-fail";
		//게시글의 내용 검증
		if(br.hasErrors()) {
			return "board/update-form";
		}
		//게시글 업데이트 수행
		boardService.update(board);
		//다시 게시글 조회 페이지로 이동하기 위해 boardId저장
		model.addAttribute("boardId",boardService.get(id).getId());
		return "board/update-success";
	}
	//게시글 삭제
	@GetMapping("delete/{id}")
	public String delete_checkPw(Model model, @PathVariable Long id,HttpSession session) {
		//로그인한 회원인지 검증
		if(session.getAttribute("memberId")==null)
			return "board/login-check";
		//자기가 작성한 글인지 검증
		if(myBoardCheck(id, session))
			return "board/my-board-check";

		//비밀번호 검증을 위한 페이지로 이동, 비밀번호를 틀렸을 경우 다시 게시글조회 페이지로 이동할 수 있도록 boardId저장
		model.addAttribute("boardId",id);
		return "board/delete-checkPw-form";
	}
	//게시글 삭제
	@PostMapping("delete/{id}")
	public String delete(@PathVariable Long id,@RequestParam String checkPw,HttpSession session) {
		//로그인 검증
		if(session.getAttribute("memberId")==null)
			return "board/login-check";
		//자기자신의 글인지 검증
		if(myBoardCheck(id, session))
			return "board/my-board-check";
		//게시글의 비밀번호 검증 비밀번호검증을 통과하면 게시글 삭제 검증실패 시 실패 페이지로 이동
		if(boardService.get(id).getPassword().equals(checkPw))
			boardService.delete(id);
		else
			return "board/delete-checkPw-fail";
		//삭제 완료시 사용자에게 삭제 완료라는 메세지를 보여주는 페이지로 이동
		return "board/delete-success";
	}
	//자기 자신의 글인지 확인 하는 메서드 자기자신의 글이 맞으면 true를 반환 아니면 false
	private boolean myBoardCheck(Long id, HttpSession session) {
		Board board = boardService.get(id);
		Member member = memberService.info((Long)session.getAttribute("memberId"));
		boolean myBoardCheck = !member.getUserId().equals(board.getWriter());
		return myBoardCheck;
	}
	//댓글 작성
	@PostMapping("comment/reg")
	public String comment_reg(HttpSession session,@RequestParam Long boardId,@Validated Comment comment, BindingResult br,
			@RequestParam int page, @RequestParam int maxResult, @RequestParam String field, @RequestParam String query) {
		//로그인 한 회원인지 검증
		if(session.getAttribute("memberId")==null)
			return "board/login-check";
		//댓글 내용이 빈문자열 인지 검증  
		if(br.hasErrors())
			return "board/comment-fail";
		//작성한 댓글에 댓글을 작성한 사용자의 아이디를 세팅하기 위한 로직 commentReg 매서드 호출 시 필요한 매개변수
		Member member = memberService.info((Long)session.getAttribute("memberId"));
		String userId = member.getUserId();
		//작성한 댓글 등록하는 매서드 호출
		boardService.commentReg(comment,userId,boardId);
		//댓글 작성이후 댓글을 작성했던 게시글의 페이지로 이동 여기서 목록으로 다시 이동했을 때 기존에 보고있던 페이지로 이동하기 위해 필요한 파라미터를 넘겨줌
		return "redirect:/board/board/"+boardId+"?page="+page+"&maxResult="+maxResult+"&field="+field+"&query="+query;
	}

	@GetMapping("likesUp/{id}")
	public String likesUp(@PathVariable("id") Long boardId,HttpSession session,@RequestParam(defaultValue = "1")int page) {
		if(session.getAttribute("likeOrDislikePage")==null)
			session.setAttribute("likeOrDislikePage", page);
		if(session.getAttribute("memberId")==null)
			return "board/login-check";

		session.setAttribute("likeOrDislike",true);
		boardService.likesUp(boardId,(Long)session.getAttribute("memberId"));
		return "redirect:/board/board/"+boardId;
	}
	@GetMapping("dislikesUp/{id}")
	public String dislikesUp(@PathVariable("id") Long boardId,HttpSession session,@RequestParam(defaultValue = "1")int page) {
		if(session.getAttribute("likeOrDislikePage")==null)
			session.setAttribute("likeOrDislikePage", page);
		if(session.getAttribute("memberId")==null)
			return "board/login-check";

		session.setAttribute("likeOrDislike",true);
		boardService.dislikesUp(boardId,(Long)session.getAttribute("memberId"));
		return "redirect:/board/board/"+boardId;
	}
	@GetMapping("likesDown/{id}")
	public String likesDown(@PathVariable("id") Long boardId,HttpSession session,@RequestParam(defaultValue = "1")int page) {
		if(session.getAttribute("likeOrDislikePage")==null)
			session.setAttribute("likeOrDislikePage", page);
		if(session.getAttribute("memberId")==null)
			return "board/login-check";

		session.setAttribute("likeOrDislike",true);
		boardService.likesDown(boardId,(Long)session.getAttribute("memberId"));
		return "redirect:/board/board/"+boardId;
	}
	@GetMapping("dislikesDown/{id}")
	public String dislikesDown(@PathVariable("id") Long boardId,HttpSession session,@RequestParam(defaultValue = "1")int page) {
		if(session.getAttribute("likeOrDislikePage")==null)
			session.setAttribute("likeOrDislikePage", page);
		if(session.getAttribute("memberId")==null)
			return "board/login-check";

		session.setAttribute("likeOrDislike",true);
		boardService.dislikesDown(boardId,(Long)session.getAttribute("memberId"));
		return "redirect:/board/board/"+boardId;
	}
}
