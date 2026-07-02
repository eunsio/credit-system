package cn.in12.apply.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import cn.in12.apply.vo.GradeStatItem;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "管理后台仪表盘统计数据")
public class DashboardVO {

    @Schema(description = "学生总人数")
    private Long totalStudents;

    @Schema(description = "申领总次数")
    private Long totalApplies;

    @Schema(description = "待审核数量")
    private Long pendingCount;

    @Schema(description = "已批准数量")
    private Long approvedCount;

    @Schema(description = "已驳回数量")
    private Long rejectedCount;

    @Schema(description = "已批准总学分")
    private BigDecimal totalCredits;

    @Schema(description = "各状态分布（饼图）")
    private List<StatusItem> statusDistribution;

    @Schema(description = "各学年申领统计")
    private List<YearItem> yearStats;

    @Schema(description = "各年级获奖统计")
    private List<GradeStatItem> gradeStats;

    @Schema(description = "学生学分排名 Top10")
    private List<TopStudentItem> topStudents;
}
