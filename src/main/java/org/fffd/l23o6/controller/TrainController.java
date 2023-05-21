package org.fffd.l23o6.controller;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;

import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.mapper.TrainMapper;
import org.fffd.l23o6.pojo.vo.PagedResult;
import org.fffd.l23o6.pojo.vo.train.AddTrainRequest;
import org.fffd.l23o6.pojo.vo.train.AdminTrainVO;
import org.fffd.l23o6.pojo.vo.train.ListTrainRequest;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.fffd.l23o6.pojo.vo.train.TrainVO.TicketInfo;
import org.fffd.l23o6.service.TrainService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/v1/")
@AllArgsConstructor
public class TrainController {

    private final TrainService trainService;

    @GetMapping("train")
    public CommonResponse<List<TrainVO>> listTrains(@Valid @RequestParam ListTrainRequest request) {
        return CommonResponse.success(trainService.listTrains(request.getStartStationId(), request.getEndStationId(), request.getDate()));
    }

    @GetMapping("train/{trainId}")
    public CommonResponse<TrainVO> getTrain(@PathVariable Long trainId) {
        return CommonResponse.success(new TrainVO(1L,"1",1L,1L,new Date(System.currentTimeMillis()),new Date(System.currentTimeMillis()),new ArrayList<TicketInfo>()));
    }

    @PostMapping("admin/train")
    public CommonResponse<?> addTrain(@Valid @RequestBody AddTrainRequest request){
        trainService.addTrain(request.getName(), request.getRouteId(), 0, request.getDate(), request.getArrivalTimes(), request.getDepartureTimes());
        return CommonResponse.success();
    }

    @GetMapping("admin/train")
    public CommonResponse<List<AdminTrainVO>> listTrainsAdmin() {
        return null;
    }

    @GetMapping("admin/train/{trainId}")
    public CommonResponse<AdminTrainVO> getTrainAdmin(@PathVariable Long trainId){
        return CommonResponse.success();
    }
}
