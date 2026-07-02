package cn.in12.apply.controller;

import cn.in12.apply.service.StatService;
import cn.in12.apply.vo.DashboardVO;
import cn.in12.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "数据统计图表")
@RestController
@RequestMapping("/apply")
public class StatController {

    @Autowired
    private StatService statService;

    @Operation(summary = "获取仪表盘统计数据（管理员）")
    @GetMapping("/stat/dashboard")
    public Result<DashboardVO> getDashboard() {
        DashboardVO vo = statService.getDashboard();
        return Result.success(vo);
    }
}
