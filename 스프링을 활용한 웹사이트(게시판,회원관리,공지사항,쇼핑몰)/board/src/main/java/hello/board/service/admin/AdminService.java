package hello.board.service.admin;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import hello.board.domain.AdminComment;
import hello.board.domain.Member;
import hello.board.domain.Notice;
import hello.board.domain.SellerReg;
import hello.board.repository.Notice.JpaNoticeRepository;
import hello.board.repository.Notice.NoticeRepository;
import hello.board.repository.admin.SellerRegRepository;
import hello.board.repository.board.adminComment.AdminCommentRepository;
import hello.board.repository.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
//관리자 기능을 구현하기 위한 서비스 계층
@Service
@RequiredArgsConstructor
public class AdminService {
	private final AdminCommentRepository adminCommentRepository;
	private final NoticeRepository noticeRepository;
	private final JpaNoticeRepository jpaNoticeRepository;
	private final SellerRegRepository sellerRegRepository;
	private final MemberRepository memberRepository;
	//관리자 답글 등록
	public void adminCommentRegister(AdminComment adminComment) {
		adminCommentRepository.save(adminComment);
	}
	//관리자 답글 목록
	public List<AdminComment> adminComments() {
		return adminCommentRepository.findAll();
	}
	//관리자 답글 조회
	public AdminComment getAdminComment(Long id) {
		return adminCommentRepository.findById(id).orElse(null);
	}
	//관리자 답글 수정
	@Transactional
	public void updateComment(AdminComment adminComment) {
		AdminComment findAdminComment = adminCommentRepository.findById(adminComment.getId()).orElse(null);
		findAdminComment.setSubject(adminComment.getSubject());
		findAdminComment.setContent(adminComment.getContent());
	}
	//관리자 답글 삭제
	public void deleteComment(Long id) {
		adminCommentRepository.deleteById(id);
	}
	//홈페이지 공지사항 목록 (5개만 출력)
	public List<Notice> getHomeNoticeList() {
		return jpaNoticeRepository.findAll();
	}
	//공지사항 조회
	public Notice getNotice(Long id) {
		return noticeRepository.findById(id).orElse(null);
	}
	//공지사항 등록
	public void noticeReg(Notice notice) {
		notice.setInputDate(new Date());
		notice.setReadCount(0);
		noticeRepository.save(notice);
	}
	//공지사항 조회수 1씩증가
	@Transactional
	public void noticeRcUp(Long id) {
		Notice notice = noticeRepository.findById(id).orElse(null);
		int readCount = notice.getReadCount();
		notice.setReadCount(readCount+1);
	}
	//공지사항 수정
	@Transactional
	public void noticeUpdate(Long id, Notice notice) {
		Notice findNotice = noticeRepository.findById(id).orElse(null);
		findNotice.setSubject(notice.getSubject());
		findNotice.setContent(notice.getContent());
	}
	//공지사항 삭제
	public void noticeDelete(Long id) {
		noticeRepository.deleteById(id);
	}
	//공지사항 전체 목록
	public List<Notice> getNoticeList() {
		return jpaNoticeRepository.findAllList();
	}
	public void sellerReg(SellerReg sellerReg) {
		sellerRegRepository.save(sellerReg);
	}
	public List<SellerReg> sellerRegList() {
		return sellerRegRepository.findAll();
	}
	@Transactional
	public void sellerRegAccept(Long sellerRegId) {
		SellerReg sellerReg = sellerRegRepository.findById(sellerRegId).orElse(null);
		Member member = memberRepository.findById(sellerReg.getMemberId()).orElse(null);
		member.setAddress(sellerReg.getAddress());
		member.setPhone(sellerReg.getPhone());
		member.setSeller(true);
		sellerRegRepository.deleteById(sellerRegId);
	}
}
