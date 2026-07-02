package cn.in12.apply.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sys_credit_apply")
@Schema(description = "创新学分申领记录")
public class CreditApply implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "申请学生ID")
    private Long userId;
    
    @Schema(description = "获奖名称/项目名称")
    private String awardTitle;
    
    @Schema(description = "获奖证明照片/文件链接")
    private String imageUrl;
    
    @Schema(description = "申请学分分值")
    private BigDecimal creditScore;
    
    @Schema(description = "审核状态: 0-待审核, 1-已批准, 2-已驳回")
    private Integer status;
    
    @Schema(description = "审批备注/驳回原因")
    private String auditRemark;
    
    @Schema(description = "审批时间")
    private LocalDateTime auditTime;
    
    @Schema(description = "审批人ID")
    private Long auditorId;
    
    @Schema(description = "所属学年 (用于获奖统计)")
    private String academicYear;
    
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
