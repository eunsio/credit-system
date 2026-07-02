package cn.in12.user.controller;

import cn.in12.common.Result;
import cn.in12.common.entity.User;
import cn.in12.user.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理模块")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "根据ID查询用户信息")
    @GetMapping("/{id}")
    public Result<User> getUserInfo(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    @Operation(summary = "修改个人基本信息")
    @PutMapping("/update")
    public Result<String> updateUserInfo(@RequestBody User user) {
        if (user.getId() == null) {
            return Result.error("用户ID不能为空");
        }
        User updateData = new User();
        updateData.setId(user.getId());
        updateData.setNickname(user.getNickname());
        updateData.setAvatar(user.getAvatar());
        updateData.setPhone(user.getPhone());
        updateData.setBio(user.getBio());
        updateData.setContactInfo(user.getContactInfo());

        boolean success = userService.updateById(updateData);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    @Operation(summary = "修改用户密码")
    @PutMapping("/password")
    public Result<String> updatePassword(@RequestParam("id") Long id, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!user.getPassword().equals(oldPassword)) {
            return Result.error("原密码错误");
        }
        user.setPassword(newPassword);
        userService.updateById(user);
        return Result.success("密码修改成功");
    }

    // ==================== 指导教师管理 ====================

    @Operation(summary = "查看所有指导教师列表")
    @GetMapping("/instructors")
    public Result<Page<User>> listInstructors(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<User> result = userService.lambdaQuery()
                .eq(User::getRole, "INSTRUCTOR")
                .orderByAsc(User::getId)
                .page(new Page<>(page, size));
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(result);
    }

    @Operation(summary = "根据ID查看指导教师详情")
    @GetMapping("/instructor/{id}")
    public Result<User> getInstructorById(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return Result.error("指导教师不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @Operation(summary = "新增指导教师")
    @PostMapping("/instructor")
    public Result<String> addInstructor(@RequestBody User user) {
        user.setRole("INSTRUCTOR");
        boolean success = userService.save(user);
        return success ? Result.success("新增指导教师成功") : Result.error("新增失败");
    }

    @Operation(summary = "修改指导教师信息")
    @PutMapping("/instructor")
    public Result<String> updateInstructor(@RequestBody User user) {
        if (user.getId() == null) {
            return Result.error("ID不能为空");
        }
        User exist = userService.getById(user.getId());
        if (exist == null || !"INSTRUCTOR".equals(exist.getRole())) {
            return Result.error("指导教师不存在");
        }
        User updateData = new User();
        updateData.setId(user.getId());
        updateData.setUsername(user.getUsername());
        updateData.setPassword(user.getPassword());
        updateData.setNickname(user.getNickname());
        updateData.setAvatar(user.getAvatar());
        updateData.setPhone(user.getPhone());
        updateData.setBio(user.getBio());
        updateData.setContactInfo(user.getContactInfo());
        updateData.setRole("INSTRUCTOR");
        boolean success = userService.updateById(updateData);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    @Operation(summary = "删除指导教师")
    @DeleteMapping("/instructor/{id}")
    public Result<String> deleteInstructor(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        if (user == null || !"INSTRUCTOR".equals(user.getRole())) {
            return Result.error("指导教师不存在");
        }
        boolean success = userService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    // ==================== 学生数据管理 ====================

    @Operation(summary = "查看所有学生列表")
    @GetMapping("/students")
    public Result<Page<User>> listStudents(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<User> result = userService.lambdaQuery()
                .eq(User::getRole, "STUDENT")
                .orderByAsc(User::getId)
                .page(new Page<>(page, size));
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(result);
    }
}
