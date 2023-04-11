package org.fffd.l23o6.pojo.vo.train;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "列出车次请求")
public class ListTrainRequest {
    @Schema(description = "页码", required = false)
    private Integer page;

    @Schema(description = "起点城市", required = true)
    @NotNull
    private String startCity;

    @Schema(description = "终点城市", required = true)
    @NotNull
    private String endCity;

    @Schema(description = "车次日期", required = true)
    @Pattern(regexp = "^[0-9]{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$", message = "日期格式错误")
    @NotNull
    private String date;
}
