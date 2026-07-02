package cn.in12.competition.controller;

import cn.in12.common.Result;
import cn.in12.competition.entity.Competition;
import cn.in12.competition.service.CompetitionService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "比赛信息管理")
@RestController
@RequestMapping("/competition")
public class CompetitionController {

    @Autowired
    private CompetitionService competitionService;

    @Operation(summary = "获取所有比赛信息 (学生/管理员)")
    @GetMapping("/list")
    public Result<Page<Competition>> list(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return Result.success(competitionService.lambdaQuery()
                .orderByDesc(Competition::getStartTime)
                .page(new Page<>(page, size)));
    }

    @Operation(summary = "获取当前可参加的比赛 (学生)")
    @GetMapping("/active")
    public Result<List<Competition>> getActiveCompetitions() {
        LocalDateTime now = LocalDateTime.now();
        List<Competition> activeList = competitionService.lambdaQuery()
                .le(Competition::getStartTime, now)
                .ge(Competition::getEndTime, now)
                .list();
        return Result.success(activeList);
    }

    @Operation(summary = "根据ID获取详情")
    @GetMapping("/{id}")
    public Result<Competition> getById(@PathVariable("id") Long id) {
        return Result.success(competitionService.getById(id));
    }

    @Operation(summary = "添加比赛信息 (管理员)")
    @PostMapping("/add")
    public Result<String> add(@RequestBody Competition competition) {
        boolean success = competitionService.save(competition);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    @Operation(summary = "修改比赛信息 (管理员)")
    @PutMapping("/update")
    public Result<String> update(@RequestBody Competition competition) {
        boolean success = competitionService.updateById(competition);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    @Operation(summary = "删除比赛信息 (管理员)")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable("id") Long id) {
        boolean success = competitionService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
