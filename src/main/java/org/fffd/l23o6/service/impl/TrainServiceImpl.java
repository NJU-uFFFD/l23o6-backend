package org.fffd.l23o6.service.impl;

import java.util.Date;
import java.util.List;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.fffd.l23o6.service.TrainService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {
    private final TrainDao Traindao;
    @Override
    public TrainVO getTrain(Long trainId){
        return null;
    }
    @Override
    public Page<TrainVO> listTrains(Integer page, Integer pageSize, String startCity, String endCity, String date){
        return null;
    }
    @Override
    public void addTrain(String name, Long routeId, Integer type, String date, List<Date> arrivalTimes, List<Date> departureTimes){
        TrainEntity entity = TrainEntity.builder().name(name).routeId(routeId).trainType(type).date(date).arrivalTimes(arrivalTimes).departureTimes(departureTimes).build();
        Traindao.save(entity);
    }
}
