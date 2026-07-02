package cn.in12.apply.controller;

import cn.in12.apply.entity.CreditApply;
import cn.in12.apply.service.CreditApplyService;
import cn.in12.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "创新学分申领管理")
@RestController
@RequestMapping("/apply")
public class CreditApplyController {

    @Autowired
    private CreditApplyService creditApplyService;

    /**
     * 学生自主申领创新学分
     */
    @Operation(summary = "学生提交学分申领 (学生)")
    @PostMapping("/submit")
    public Result<String> submitApply(@RequestBody CreditApply creditApply) {
        if (creditApply.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        creditApply.setStatus(0); // 初始状态为待审核
        boolean success = creditApplyService.save(creditApply);
        return success ? Result.success("申领已提交，请等待审核") : Result.error("提交失败");
    }

    /**
     * 学生查看当前创新学分拥有情况
     */
    @Operation(summary = "查询个人已获得的创新学分总分 (学生)")
    @GetMapping("/total-score/{userId}")
    public Result<BigDecimal> getTotalScore(@PathVariable("userId") Long userId) {
        List<CreditApply> list = creditApplyService.lambdaQuery()
                .eq(CreditApply::getUserId, userId)
                .eq(CreditApply::getStatus, 1) // 只统计已批准的
                .list();
        
        BigDecimal total = list.stream()
                .map(CreditApply::getCreditScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return Result.success(total);
    }

    /**
     * 学生查看当前学年内个人获奖情况
     */
    @Operation(summary = "查看个人已获奖记录 (学生)")
    @GetMapping("/my-awards")
    public Result<List<CreditApply>> getMyAwards(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "academicYear", required = false) String academicYear) {
        var wrapper = creditApplyService.lambdaQuery()
                .eq(CreditApply::getUserId, userId)
                .eq(CreditApply::getStatus, 1);
        if (academicYear != null && !academicYear.isEmpty()) {
            wrapper.eq(CreditApply::getAcademicYear, academicYear);
        }
        List<CreditApply> awards = wrapper.orderByDesc(CreditApply::getCreateTime).list();
        return Result.success(awards);
    }

    @Operation(summary = "查看个人全部申领记录（含待审核）")
    @GetMapping("/my-all/{userId}")
    public Result<List<CreditApply>> getMyAllApplies(@PathVariable("userId") Long userId) {
        List<CreditApply> list = creditApplyService.lambdaQuery()
                .eq(CreditApply::getUserId, userId)
                .orderByDesc(CreditApply::getCreateTime)
                .list();
        return Result.success(list);
    }

    /**
     * 管理员审批接口
     */
    @Operation(summary = "审批学生申领 (管理员)")
    @PutMapping("/audit")
    public Result<String> auditApply(
            @RequestParam("id") Long id,
            @RequestParam("status") Integer status,
            @RequestParam("remark") String remark,
            @RequestParam("auditorId") Long auditorId) {
        
        CreditApply apply = creditApplyService.getById(id);
        if (apply == null) {
            return Result.error("申领记录不存在");
        }
        
        apply.setStatus(status);
        apply.setAuditRemark(remark);
        apply.setAuditorId(auditorId);
        apply.setAuditTime(LocalDateTime.now());
        
        boolean success = creditApplyService.updateById(apply);
        return success ? Result.success("审批完成") : Result.error("审批失败");
    }

    /**
     * 管理员查看所有申领记录
     */
    @Operation(summary = "查看所有申领记录 (管理员)")
    @GetMapping("/all")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<CreditApply>> getAllApplies(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return Result.success(creditApplyService.lambdaQuery()
                .orderByDesc(CreditApply::getCreateTime)
                .page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size)));
    }

    @Operation(summary = "删除申领记录（管理员，含图片清理）")
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteApply(@PathVariable("id") Long id) {
        CreditApply apply = creditApplyService.getById(id);
        if (apply == null) {
            return Result.error("申领记录不存在");
        }

        if (apply.getImageUrl() != null && !apply.getImageUrl().isEmpty()) {
            String[] urls = apply.getImageUrl().split(",");
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            for (String url : urls) {
                String filename = url.substring(url.lastIndexOf("/") + 1);
                java.io.File file = new java.io.File(uploadDir + filename);
                if (file.exists()) {
                    file.delete();
                }
            }
        }

        boolean success = creditApplyService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @Operation(summary = "撤回待审核的申领记录（学生）")
    @DeleteMapping("/withdraw/{id}")
    public Result<String> withdrawApply(@PathVariable("id") Long id) {
        CreditApply apply = creditApplyService.getById(id);
        if (apply == null) {
            return Result.error("申领记录不存在");
        }
        if (apply.getStatus() != 0) {
            return Result.error("该申领记录状态不允许撤回");
        }
        creditApplyService.removeById(id);
        return Result.success("撤回成功");
    }
}
