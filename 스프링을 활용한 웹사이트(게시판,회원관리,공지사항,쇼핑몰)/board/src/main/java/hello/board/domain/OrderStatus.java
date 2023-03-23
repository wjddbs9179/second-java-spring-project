package hello.board.domain;
//주문상태를 표시하기 위한 enum
public enum OrderStatus {
//	RECEIVED,		//주문접수,
//	PENDING,		//결제대기,
//	COMPLETED,		//결제완료,
//	PREPARING,		//상품준비중,
//	SHIPPED,		//배송중,
//	DELIVERED,		//배송완료,
//	CANCEL_REQUEST,	//취소요청,
//	CANCELLED		//취소완료
	
    RECEIVED("주문접수"),
    PENDING("결제대기"),
    COMPLETED("결제완료"),
    PREPARING("상품준비중"),	
    SHIPPED("배송중"),
    DELIVERED("배송완료"),
    CANCEL_REQUEST("취소요청"),
    CANCELLED("취소완료");
	
    private final String korName;
	
    OrderStatus(String korName) {
        this.korName = korName;
    }
	
    public String getKorName() {
        return korName;
    }
}
