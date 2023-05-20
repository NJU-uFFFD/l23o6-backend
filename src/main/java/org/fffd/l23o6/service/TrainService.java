package org.fffd.l23o6.service;

import java.util.Date;
import java.util.List;

import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.springframework.data.domain.Page;

public interface TrainService {
    public TrainVO getTrain(Long trainId);
    public Page<TrainVO> listTrains(Integer page, Integer pageSize, String startCity, String endCity, String date);
    public void addTrain(String name, Long routeId, Integer type, String date, List<Date> arrivalTimes, List<Date> departureTimes);
}
