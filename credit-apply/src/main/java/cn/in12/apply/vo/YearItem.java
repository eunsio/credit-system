package cn.in12.apply.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "学年统计项")
public class YearItem {

    @Schema(description = "学年")
    private String academicYear;

    @Schema(description = "申领总数")
    private Long applyCount;

    @Schema(description = "已批准数量")
    private Long approvedCount;

    @Schema(description = "已批准总学分")
    private BigDecimal totalCredits;
}
