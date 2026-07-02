package cn.in12.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_community_post")
@Schema(description = "技术社区帖子信息")
public class CommunityPost implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;
    
    @Schema(description = "发布人ID")
    private Long userId;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "详情内容")
    private String content;
    
    @Schema(description = "信息类型: 0-技术求助, 1-官方支持公告")
    private Integer postType;
    
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
