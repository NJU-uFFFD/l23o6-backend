package org.fffd.l23o6.controller;

import java.util.Date;
import java.util.ArrayList;

import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import org.fffd.l23o6.pojo.vo.PagedResult;
import org.fffd.l23o6.pojo.vo.train.ListTrainRequest;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.fffd.l23o6.pojo.vo.train.TrainVO.TicketInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/")
public class TrainController {
    @GetMapping("train")
    public CommonResponse<PagedResult<TrainVO>> listTrains(@Valid @RequestParam ListTrainRequest request) {
        return null;
    }

    @GetMapping("train/{trainId}")
    public CommonResponse<TrainVO> getTrain(@PathVariable Integer trainId) {
        return CommonResponse.success(new TrainVO(1L,"1","1","1",new Date(System.currentTimeMillis()),new Date(System.currentTimeMillis()),false,new ArrayList<TicketInfo>()));
    }
}
