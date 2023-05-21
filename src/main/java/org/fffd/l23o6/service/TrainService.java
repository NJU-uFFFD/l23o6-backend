package org.fffd.l23o6.service;

import java.util.Date;
import java.util.List;

import org.fffd.l23o6.pojo.vo.train.TrainVO;

public interface TrainService {
    public TrainVO getTrain(Long trainId);
    public List<TrainVO> listTrains(Integer startStationId, Integer endStationId, String date);
    public void addTrain(String name, Long routeId, Integer type, String date, List<Date> arrivalTimes, List<Date> departureTimes);
}
