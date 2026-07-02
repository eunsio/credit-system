package cn.in12.apply.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "学生学分排名项")
public class TopStudentItem {

    @Schema(description = "学生ID")
    private Long userId;

    @Schema(description = "学生昵称")
    private String nickname;

    @Schema(description = "累计已批准学分")
    private BigDecimal totalCredits;
}
