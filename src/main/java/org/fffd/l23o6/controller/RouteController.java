package org.fffd.l23o6.controller;

import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.fffd.l23o6.pojo.vo.route.AddRouteRequest;
import org.fffd.l23o6.service.RouteService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;

    @PostMapping("route")
    public CommonResponse<?> register(@Valid @RequestBody AddRouteRequest request) {
        // Throws BizException if register failed.
        routeService.addRoute(request.getName(), request.getStationIds());

        return CommonResponse.success();
    }
}