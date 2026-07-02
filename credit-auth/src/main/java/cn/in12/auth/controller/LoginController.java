package cn.in12.auth.controller;

import cn.in12.auth.service.AuthService;
import cn.in12.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "登录接口")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String password = body.get("password");
            if (username == null || password == null) {
                return Result.error("用户名和密码不能为空");
            }
            Map<String, Object> data = authService.login(username, password);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "测试接口")
    @GetMapping("/login")
    public Result<String> testLogin() {
        return Result.success("请使用 POST 方法进行登录测试");
    }
}
