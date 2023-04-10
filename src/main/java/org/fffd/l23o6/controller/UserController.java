package org.fffd.l23o6.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import cn.dev33.satoken.stp.StpUtil;
import org.fffd.l23o6.service.UserService;
import org.fffd.l23o6.pojo.Response;
import org.fffd.l23o6.pojo.LoginRequest;

@RestController
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("session")
    public Response<?> login(@RequestBody LoginRequest request) {
        try {
            userService.login(request.getUsername(), request.getPassword());
            StpUtil.login(request.getUsername());
        } catch (Exception e) {
            return Response.builder().status(-1).msg(e.getMessage()).data(null).build();
        }
        return Response.builder().status(0).msg("OK").data(null).build();
    }
}