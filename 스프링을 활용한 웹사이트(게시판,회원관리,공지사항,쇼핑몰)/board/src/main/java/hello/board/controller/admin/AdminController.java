package hello.board.controller.admin;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import hello.board.domain.AdminComment;
import hello.board.domain.Member;
import hello.board.domain.Notice;
import hello.board.domain.SellerReg;
import hello.board.service.admin.AdminService;
import hello.board.service.board.BoardService;
import hello.board.service.member.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
//컨트롤러 클래스, 의존관계 자동주입 받기 위해 RequiredArgsConstructor 사용
@Controller
@RequiredArgsConstructor
public class AdminController {
	
	private final AdminService adminService;
	private final BoardService boardService;
	private final MemberService memberService;
	
	//관리자 답글 등록 페이지 url매핑
	@GetMapping("/adminComment/reg")
	public String adminCommentReg(Model model, HttpSession session) {
		//관리자 인지 확인하고 관리자가 아니라면 notAdmin 페이지로 이동
		boolean isAdmin = (boolean)session.getAttribute("isAdmin");
		if(!isAdmin)
			return "admin/notAdmin";
		//관리자가 맞으면 adminComment를 모델에 저장하고 adminComment작성 페이지로 이동
		model.addAttribute("adminComment",new AdminComment());
		return "admin/adminComment-reg-form";
	}
	//관리자 답글을 작성하고 등록버튼 클릭 시 수행되는 로직
	@PostMapping("/adminComment/reg")
	public String adminCommentReg(@RequestParam Long boardId,@RequestParam String userId ,AdminComment adminComment,HttpSession session) {
		//관리자 인지 확인하고 관리자가 아니라면 notAdmin 페이지로 이동
		boolean isAdmin = (boolean)session.getAttribute("isAdmin");
		if(!isAdmin)
			return "admin/notAdmin";
		//관리자가 맞으면 boardID와 userId, 등록날짜를 세팅하고 adminService의 관리자 답글을 등록하는 매서드 호출
		adminComment.setBoardId(boardId);
		adminComment.setUserId(userId);
		adminComment.setInputDate(new Date());
		adminService.adminCommentRegister(adminComment);
		return "redirect:/board/board/"+boardId;
	}
	
	//관리자 답글을 삭제하는 로직
	@GetMapping("/comment/delete")
	public String commentDelete(
			@RequestParam Long commentId,
			@RequestParam Long boardId,
			@RequestParam int page,
			@RequestParam int maxResult,
			@RequestParam String field,
			@RequestParam String query,
			Model model,HttpSession session
			) {
		//관리자가 맞는지 확인하고 아니라면 notAdmin페이지로 이동
		boolean isAdmin = (boolean)session.getAttribute("isAdmin");
		if(!isAdmin)
			return "admin/notAdmin";
		//관리자가 맞다면 adminComment를 삭제하는 매서드 호출
		boardService.commentDelete(commentId);
		//삭제 후 관리자 답글을 작성한 게시글로 이동하기 위해 필요한 변수들 모델에 저장
		model.addAttribute("boardId",boardId);
		model.addAttribute("page",page);
		model.addAttribute("maxResult",maxResult);
		model.addAttribute("field",field);
		model.addAttribute("query",query);
		return "admin/comment-delete-success";
	}
	//관리자 답글을 조회하는 페이지로 이동하기 위한 url매핑
	@GetMapping("/adminComment/adminComment/{id}")
	public String adminComment(Model model, @PathVariable Long id, HttpSession session,
			@RequestParam(defaultValue = "1") String page,
			@RequestParam(defaultValue = "10") int maxResult,
			@RequestParam(defaultValue = "subject") String field,
			@RequestParam(defaultValue = "") String query) {
		//관리자 답글을 조회하고 목록으로 돌아갔을 때 보고있던 페이지와 검색어를 유지시키기 위해 변수들 model에 저장
		model.addAttribute("page",page);
		model.addAttribute("maxResult",maxResult);
		model.addAttribute("field",field);
		model.addAttribute("query",query);
		//관리자 답글을 조회 가능한 사람(관리자,게시글의 작성자)인지 검증
		AdminComment adminComment = adminService.getAdminComment(id);
		Long memberId = (Long)session.getAttribute("memberId");
		Member member = memberService.info(memberId);
		boolean isAdmin = (boolean)session.getAttribute("isAdmin");
		//관리자 답글을 조회 가능한 사람이면 관리자답글 페이지로 이동, 아니면 조회 실패 페이지로 이동
		if(member.getUserId().equals(adminComment.getUserId())||isAdmin) {
			model.addAttribute("adminComment",adminComment);
			return "admin/comment";
		}else {
			return "admin/comment-fail";
		}
	}
	//관리자 답글을 수정하기 위한 url매핑
	@GetMapping("/admin/adminComment/update/{id}")
	public String adminCommentUpdate(Model model, @PathVariable Long id, HttpSession session) {
		//현재 로그인한 회원이 관리자 인지 확인하고 아니면 notAdmin페이지로 이동
		if(!(boolean)session.getAttribute("isAdmin"))
			return "admin/notAdmin";
		//관리자가 맞다면 해당 관리자 답글을 model에 저장 하고 update페이지로 이동
		model.addAttribute("adminComment",adminService.getAdminComment(id));
		return "admin/adminComment-update";
	}
	//관리자 답글을 수정한 이후 저장할 때 사용될 url
	@PostMapping("/admin/adminComment/update/{id}")
	public String adminCommentUpdate(@PathVariable Long id, AdminComment adminComment,HttpSession session) {
		//관리자인지 확인하고 아니라면 notAdmin페이지로 이동
		if(!(boolean)session.getAttribute("isAdmin"))
			return "admin/notAdmin";
		//관리자가 맞다면 관리자 답글을 업데이트 하는 매서드 호출 후 다시 해당 관리자답글 페이지로 이동
		adminService.updateComment(adminComment);
		return "redirect:/adminComment/adminComment/"+id;
	}
	//관리자 답글을 삭제하기 위한 url
	@GetMapping("/admin/adminComment/delete/{id}")
	public String adminCommentDelete(@PathVariable Long id,HttpSession session) {
		//관리자가 맞는지 확인하고 아니라면 notAdmin페이지로 이동
		boolean isAdmin = (boolean)session.getAttribute("isAdmin");
		if(!isAdmin)
			return "admin/notAdmin";
		//관리자가 맞다면 관리자 답글을 삭제하는 매서드 호출 후 게시판 메인페이지로 이동
		adminService.deleteComment(id);
		return "redirect:/board/list";
	}
	//공지사항을 등록하는 url
	@GetMapping("/admin/notice/reg")
	public String adminNoticeReg(Model model,HttpSession session) {
		//관리자가 맞는지 확인하고 아니라면 notAdmin페이지로 이동
		boolean isAdmin = (boolean)session.getAttribute("isAdmin");
		if(!isAdmin)
			return "admin/notAdmin";
		//관리자가 맞으면 타임리프 th:object, th:field기능을 사용하기 위해 새로운 notice객체를 생성해서 model에 저장하고 공지사항 내용을 작성하는 페이지로 이동
		model.addAttribute("notice",new Notice());
		return "admin/notice-reg";
	}
	//공지사항을 등록하는 url
	@PostMapping("/admin/notice/reg")
	public String adminNoticeReg(Notice notice,HttpSession session) {
		//관리자가 맞는지 확인하고 아니라면 notAdmin페이지로 이동
		boolean isAdmin = (boolean)session.getAttribute("isAdmin");
		if(!isAdmin)
			return "admin/notAdmin";
		//관리자가 맞다면 공지사항을 등록하는 매서드 호출 후 공지사항 목록페이지로 이동
		adminService.noticeReg(notice);
		return "redirect:/notice/list";
	}
	//공지사항을 조회하는 url
	@GetMapping("/notice/{id}")
	public String notice(@PathVariable Long id, Model model) {
		//공지사항 조회시 조회수 증가
		adminService.noticeRcUp(id);
		//페이지에 공지사항 내용을 출력하기 위해 model에 공지사항 객체를 id로 찾아와서 저장
		model.addAttribute("notice",adminService.getNotice(id));
		return "admin/notice";
	}
	//공지사항 수정을 위한 url
	@GetMapping("/notice/update/{id}")
	public String noticeUpdate(@PathVariable Long id, Model model,HttpSession session) {
		//관리자가 아니라면 notAdmin페이지로 이동
		boolean isAdmin = (boolean)session.getAttribute("isAdmin");
		if(!isAdmin)
			return "admin/notAdmin";
		//관리자가 맞으면 notice객체를 id로 찾아와서 model에 저장한 후 notice-update페이지로 이동
		model.addAttribute("notice",adminService.getNotice(id));
		return "admin/notice-update";
	}
	//공지사항 수정을 위한 url
	@PostMapping("/notice/update/{id}")
	public String noticeUpdate(@PathVariable Long id, Notice notice,HttpSession session) {
		//관리자가 맞는지 확인하고 아니라면 notAdmin 페이지로 이동
		//Post방식 요청도 이렇게 처리하는 이유는 사용자의 요청 조작을 방지하기 위함. 실제로 포스트맨 같은 프로그램으로 요청 조작가능.
		boolean isAdmin = (boolean)session.getAttribute("isAdmin");
		if(!isAdmin)
			return "admin/notAdmin";
		//공지사항을 업데이트 하는 매서드 호출 후 해당 공지사항을 다시 조회하는 페이지로 이동
		adminService.noticeUpdate(id,notice);
		return "redirect:/notice/"+id;
	}
	//공지사항 삭제를 위한 url
	@GetMapping("/notice/delete/{id}")
	public String noticeDelete(@PathVariable Long id,HttpSession session) {
		//관리자인지 아닌지 확인하고 아니면 notAdmin페이지로 이동
		boolean isAdmin = (boolean)session.getAttribute("isAdmin");
		if(!isAdmin)
			return "admin/notAdmin";
		//관리자 이면 공지사항을 삭제하는 매서드 호출
		adminService.noticeDelete(id);
		return "redirect:/member/home";
	}
	//공지사항 전체목록 페이지
	@GetMapping("/notice/list")
	public String noticeList(Model model) {
		//공지사항 전체목록을 model에 저장 하고 notice-list페이지로 이동
		model.addAttribute("noticeList", adminService.getNoticeList());
		return "admin/notice-list";
	}
	
	@GetMapping("/admin/seller/reg")
	public String sellerReg(Model model,HttpSession session) {
		if((boolean)session.getAttribute("isSeller"))
			return "admin/isSeller";
		model.addAttribute("sellerReg",new SellerReg());
		return "admin/seller-reg";
	}
	@PostMapping("/admin/seller/reg")
	public String sellerReg(HttpSession session,SellerReg sellerReg) {
		if((boolean)session.getAttribute("isSeller"))
			return "admin/isSeller";
		sellerReg.setMemberId((Long)session.getAttribute("memberId"));
		adminService.sellerReg(sellerReg);
		return "redirect:/member/home";
	}
	@GetMapping("/admin/seller/reg/list")
	public String sellerRegList(Model model,HttpSession session) {
		if(!(boolean)session.getAttribute("isAdmin"))
			return "admin/notAdmin";
		model.addAttribute("sellerRegList",adminService.sellerRegList());
		return "admin/seller-reg-list";
	}
	@GetMapping("/admin/seller/reg/accept/{id}")
	public String sellerRegAccept(@PathVariable("id")Long sellerRegId,HttpSession session) {
		if(!(boolean)session.getAttribute("isAdmin"))
			return "admin/notAdmin";
		adminService.sellerRegAccept(sellerRegId);
		return "redirect:/admin/seller/reg/list";
	}
	@GetMapping("/admin/sellerList")
	public String sellerList(HttpSession session,Model model) {
		if(!(boolean)session.getAttribute("isAdmin"))
			return "admin/notAdmin";
		model.addAttribute("sellerList", memberService.sellerList());
			return "admin/sellerList";
	}
	@GetMapping("/admin/seller/cancel/{id}")
	public String sellerCancel(@PathVariable("id")Long memberId, HttpSession session) {
		if(!(boolean)session.getAttribute("isAdmin"))
			return "admin/notAdmin";
		memberService.sellerCancel(memberId);
		return "redirect:/admin/sellerList";
	}
}
