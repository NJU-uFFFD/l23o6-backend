package org.fffd.l23o6.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.fffd.l23o6.dao.OrderDao;
import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.enum_.order.OrderStatus;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.mapper.OrderMapper;
import org.fffd.l23o6.pojo.entity.OrderEntity;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.vo.order.OrderVO;
import org.fffd.l23o6.service.OrderService;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;
import org.springframework.stereotype.Service;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderDao orderDao;
    private final UserDao userDao;
    private final TrainDao trainDao;
    private final RouteDao routeDao;
    public Long createOrder(String username, Long trainId, Long fromStationId, Long toStationId, String seatType, Long seatNumber){
        Long userId = userDao.findByUsername(username).getId();
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startStationIndex = route.getStationIds().indexOf(fromStationId);
        int endStationIndex = route.getStationIds().indexOf(toStationId);
        String seat = null;
        switch(train.getTrainType()){
            case HIGH_SPEED:
                seat = GSeriesSeatStrategy.INSTANCE.allocSeat(startStationIndex, endStationIndex,GSeriesSeatStrategy.GSeriesSeatType.fromString(seatType) , train.getSeats());
                break;
            case NORMAL_SPEED:
                seat = KSeriesSeatStrategy.INSTANCE.allocSeat(startStationIndex, endStationIndex,KSeriesSeatStrategy.KSeriesSeatType.fromString(seatType) , train.getSeats());
                break;
        }
        if(seat == null){
            throw new BizException(BizError.OUT_OF_SEAT);
        }
        OrderEntity order = OrderEntity.builder().trainId(trainId).userId(userId).seat(seat).status(OrderStatus.PENDING_PAYMENT).arrivalStationId(toStationId).departureStationId(fromStationId).build();
        trainDao.save(train);
        orderDao.save(order);
        return order.getId();
    }
    public List<OrderVO> listOrders(String username){
        Long userId = userDao.findByUsername(username).getId();
        return orderDao.findByUserId(userId).stream().map(OrderMapper.INSTANCE::toOrderVO).collect(Collectors.toList());
    }
    public OrderVO getOrder(Long id){
        return OrderMapper.INSTANCE.toOrderVO(orderDao.findById(id).get());
    }
}
