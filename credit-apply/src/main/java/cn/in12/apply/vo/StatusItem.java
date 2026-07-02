package cn.in12.apply.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "状态分布项")
public class StatusItem {

    @Schema(description = "状态名称")
    private String status;

    @Schema(description = "数量")
    private Long count;
}
