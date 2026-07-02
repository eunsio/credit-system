package cn.in12.apply.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "年级统计项")
public class GradeStatItem {

    @Schema(description = "学年")
    private String academicYear;

    @Schema(description = "获奖学生人数")
    private Long studentCount;

    @Schema(description = "获奖次数")
    private Long awardCount;

    @Schema(description = "批准总学分")
    private BigDecimal totalCredits;
}
