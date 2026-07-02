package cn.in12.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_post_reply")
@Schema(description = "帖子回复信息")
public class PostReply implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "帖子ID")
    private Long postId;

    @Schema(description = "回复人ID")
    private Long userId;

    @Schema(description = "回复人昵称")
    private String nickname;

    @Schema(description = "回复内容")
    private String content;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
