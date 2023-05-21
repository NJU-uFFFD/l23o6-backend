package org.fffd.l23o6.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.mapper.TrainMapper;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.fffd.l23o6.service.TrainService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {
    private final TrainDao trainDao;
    private final RouteDao routeDao;

    @Override
    public TrainVO getTrain(Long trainId){
        return null;
    }
    @Override
    public List<TrainVO> listTrains(Integer startStationId, Integer endStationId, String date) {

        // First, get all routes contains [startCity, endCity]
        List<RouteEntity> routes = routeDao.findAll();
        List<RouteEntity> possibleRoutes = routes.stream().filter(route -> {
            int startIndex = route.getStationIds().indexOf(startStationId);
            int endIndex = route.getStationIds().indexOf(endStationId);
            return startIndex != -1 && endIndex != -1 && startIndex < endIndex;
        }).collect(Collectors.toList());

        // Get all trains on that day with the wanted routes
        List<TrainEntity> trains = trainDao.findAll();
        List<TrainEntity> possibleTrains = trains.stream().filter(train -> {
            return possibleRoutes.stream().anyMatch(route -> route.getId().equals(train.getRouteId()) && train.getDate().equals(date));
        }).collect(Collectors.toList());

        return possibleTrains.stream().map(TrainMapper.INSTANCE::toTrainVO).collect(Collectors.toList());
    }
    
    @Override
    public void addTrain(String name, Long routeId, Integer type, String date, List<Date> arrivalTimes, List<Date> departureTimes){
        TrainEntity entity = TrainEntity.builder().name(name).routeId(routeId).trainType(type).date(date).arrivalTimes(arrivalTimes).departureTimes(departureTimes).build();
        trainDao.save(entity);
    }
}
