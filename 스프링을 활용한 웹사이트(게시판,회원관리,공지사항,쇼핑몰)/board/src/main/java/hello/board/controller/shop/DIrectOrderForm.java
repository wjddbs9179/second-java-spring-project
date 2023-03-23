package hello.board.controller.shop;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DIrectOrderForm {
	@NotBlank
	private String address;
	@NotBlank
	private String phone;
	@Min(1)
	private int quantity;
}
