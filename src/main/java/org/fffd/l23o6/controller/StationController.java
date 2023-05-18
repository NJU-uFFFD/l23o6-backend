package org.fffd.l23o6.controller;

import java.util.List;
import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.fffd.l23o6.pojo.vo.station.AddStationRequest;
import org.fffd.l23o6.pojo.vo.station.StationVO;
import org.fffd.l23o6.service.StationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class StationController {
    private final StationService stationService;

    @GetMapping("station")
    public CommonResponse<List<StationVO>> listStations() {
        return CommonResponse.success(stationService.listStations());
    }

    @GetMapping("station/{stationId}")
    public CommonResponse<StationVO> getStation(@PathVariable Long stationId) {
        return CommonResponse.success(stationService.getStation(stationId));
    }

    @PostMapping("station")
    public CommonResponse<?> addStation(@Valid @RequestBody AddStationRequest request) {
        // Throws BizException if add failed.
        stationService.addStation(request.getName());
        return CommonResponse.success();
    }
}
