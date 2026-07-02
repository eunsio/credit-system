package cn.in12.competition.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_competition")
@Schema(description = "比赛信息实体")
public class Competition implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "比赛名称")
    private String title;
    
    @Schema(description = "比赛详情介绍")
    private String content;
    
    @Schema(description = "开始报名时间")
    private LocalDateTime startTime;
    
    @Schema(description = "截止日期")
    private LocalDateTime endTime;
    
    @Schema(description = "报名系统跳转链接")
    private String registrationUrl;
    
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
