package org.fffd.l23o6.pojo.vo.order;

import org.fffd.l23o6.enum_.order.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "订单状态修改")
public class PatchOrderRequest {

    @Schema(description = "订单状态", required = true)
    @NotNull
    private OrderStatus status;
}
