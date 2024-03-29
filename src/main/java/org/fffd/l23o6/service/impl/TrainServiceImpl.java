package org.fffd.l23o6.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.mapper.TrainMapper;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.vo.train.AdminTrainVO;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.fffd.l23o6.pojo.vo.train.TicketInfo;
import org.fffd.l23o6.pojo.vo.train.TrainDetailVO;
import org.fffd.l23o6.service.TrainService;
import org.fffd.l23o6.util.strategy.ticketPrice.TicketPriceStrategy;
import org.fffd.l23o6.util.strategy.trainSeat.GSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.trainSeat.KSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.trainSeat.TrainSeatStrategy;
import org.fffd.l23o6.util.strategy.trainStrategyFactory.GSeriesStrategyFactory;
import org.fffd.l23o6.util.strategy.trainStrategyFactory.KSeriesStrategyFactory;
import org.fffd.l23o6.util.strategy.trainStrategyFactory.TrainStrategyFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {
    private final TrainDao trainDao;
    private final RouteDao routeDao;

    @Override
    public TrainDetailVO getTrain(Long trainId) {
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        return TrainDetailVO.builder().id(trainId).date(train.getDate()).name(train.getName())
                .stationIds(route.getStationIds()).arrivalTimes(train.getArrivalTimes())
                .departureTimes(train.getDepartureTimes()).extraInfos(train.getExtraInfos()).build();
    }

    @Override
    public List<TrainVO> listTrains(Long startStationId, Long endStationId, String date) {

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
            return possibleRoutes.stream()
                    .anyMatch(route -> route.getId().equals(train.getRouteId()) && train.getDate().equals(date));
        }).collect(Collectors.toList());

        List<TrainVO> trainVOs = possibleTrains.stream().map(entity -> {
            RouteEntity route = routeDao.findById(entity.getRouteId()).get();
            List<TicketInfo> ticketInfos = new ArrayList<TicketInfo>();
            int startStationIndex = route.getStationIds().indexOf(startStationId);
            int endStationIndex = route.getStationIds().indexOf(endStationId);
            TrainStrategyFactory trainStrategyFactory = entity.getTrainType().equals(TrainType.HIGH_SPEED)
                    ? new GSeriesStrategyFactory()
                    : new KSeriesStrategyFactory();
            TrainSeatStrategy trainSeatStrategy = trainStrategyFactory.getTrainSeatStrategy();
            TicketPriceStrategy ticketPriceStrategy = trainStrategyFactory.getTicketPriceStrategy();
            trainSeatStrategy.getLeftSeatCount(startStationIndex, endStationIndex, entity.getSeats())
                    .forEach((type, count) -> {
                        ticketInfos.add(new TicketInfo(type.getText(), count,
                                ticketPriceStrategy.getPrice(startStationIndex, endStationIndex, type.getText())));
                    });
            ticketInfos.sort((a, b) -> b.getPrice().compareTo(a.getPrice()));
            TrainVO train = TrainVO.builder().id(entity.getId()).name(entity.getName())
                    .trainType(entity.getTrainType().getText())
                    .startStationId(startStationId)
                    .endStationId(endStationId)
                    .arrivalTime(entity.getArrivalTimes().get(endStationIndex))
                    .departureTime(entity.getDepartureTimes().get(startStationIndex))
                    .ticketInfo(ticketInfos)
                    .build();
            return train;
        }).collect(Collectors.toList());
        return trainVOs;
    }

    @Override
    public List<AdminTrainVO> listTrainsAdmin() {
        return trainDao.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(TrainMapper.INSTANCE::toAdminTrainVO).collect(Collectors.toList());
    }

    @Override
    public void addTrain(String name, Long routeId, TrainType type, String date, List<Date> arrivalTimes,
            List<Date> departureTimes) {
        TrainEntity entity = TrainEntity.builder().name(name).routeId(routeId).trainType(type)
                .date(date).arrivalTimes(arrivalTimes).departureTimes(departureTimes).build();
        RouteEntity route = routeDao.findById(routeId).get();
        if (route.getStationIds().size() != entity.getArrivalTimes().size()
                || route.getStationIds().size() != entity.getDepartureTimes().size()) {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "列表长度错误");
        }
        entity.setExtraInfos(new ArrayList<String>(Collections.nCopies(route.getStationIds().size(), "预计正点")));
        switch (entity.getTrainType()) {
            case HIGH_SPEED:
                entity.setSeats(GSeriesSeatStrategy.INSTANCE.initSeatMap(route.getStationIds().size()));
                break;
            case NORMAL_SPEED:
                entity.setSeats(KSeriesSeatStrategy.INSTANCE.initSeatMap(route.getStationIds().size()));
                break;
        }
        trainDao.saveAndFlush(entity);
    }

    @Override
    public void changeTrain(Long id, String name, Long routeId, TrainType type, String date, List<Date> arrivalTimes,
            List<Date> departureTimes) {
        TrainEntity entity = trainDao.findById(id).get();
        entity.setName(name);
        entity.setRouteId(routeId);
        entity.setTrainType(type);
        entity.setDate(date);
        entity.setArrivalTimes(arrivalTimes);
        entity.setDepartureTimes(departureTimes);
        RouteEntity route = routeDao.findById(routeId).get();
        if (route.getStationIds().size() != entity.getArrivalTimes().size()
                || route.getStationIds().size() != entity.getDepartureTimes().size()) {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "列表长度错误");
        }
        entity.setExtraInfos(new ArrayList<String>(Collections.nCopies(route.getStationIds().size(), "预计正点")));
        switch (entity.getTrainType()) {
            case HIGH_SPEED:
                entity.setSeats(GSeriesSeatStrategy.INSTANCE.initSeatMap(route.getStationIds().size()));
                break;
            case NORMAL_SPEED:
                entity.setSeats(KSeriesSeatStrategy.INSTANCE.initSeatMap(route.getStationIds().size()));
                break;
        }
        trainDao.saveAndFlush(entity);
    }

    @Override
    public void deleteTrain(Long id) {
        trainDao.deleteById(id);
    }
}
