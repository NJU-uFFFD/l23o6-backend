package org.fffd.l23o6.pojo.vo.order;

import org.fffd.l23o6.enum_.order.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "订单状态修改")
public class PatchOrderRequest {

    @Schema(description = "订单状态", required = true)
    @Pattern(regexp = "^(已支付|已取消)$", message = "状态错误")
    @NotNull
    private OrderStatus status;
}
