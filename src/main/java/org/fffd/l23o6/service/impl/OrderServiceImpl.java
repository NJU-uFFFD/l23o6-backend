package org.fffd.l23o6.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.fffd.l23o6.dao.OrderDao;
import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.pojo.enum_.OrderStatus;
import org.fffd.l23o6.pojo.enum_.Payment;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.pojo.entity.OrderEntity;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.vo.order.OrderVO;
import org.fffd.l23o6.service.OrderService;
import org.fffd.l23o6.util.strategy.credit.CreditStrategy;
import org.fffd.l23o6.util.strategy.payment.AlipayPaymentStrategy;
import org.fffd.l23o6.util.strategy.payment.PaymentStrategy;
import org.fffd.l23o6.util.strategy.payment.WeChatPaymentStrategy;
import org.fffd.l23o6.util.strategy.trainSeat.TrainSeatStrategy;
import org.fffd.l23o6.util.strategy.trainStrategyFactory.GSeriesStrategyFactory;
import org.fffd.l23o6.util.strategy.trainStrategyFactory.KSeriesStrategyFactory;
import org.fffd.l23o6.util.strategy.trainStrategyFactory.TrainStrategyFactory;
import org.springframework.stereotype.Service;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderDao orderDao;
    private final UserDao userDao;
    private final TrainDao trainDao;
    private final RouteDao routeDao;

    public Long createOrder(String username, Long trainId, Long fromStationId, Long toStationId, String seatType) {
        Long userId = userDao.findByUsername(username).getId();
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startStationIndex = route.getStationIds().indexOf(fromStationId);
        int endStationIndex = route.getStationIds().indexOf(toStationId);
        String seat = null;
        TrainSeatStrategy seatStrategy = (train.getTrainType().equals(TrainType.HIGH_SPEED)
                ? new GSeriesStrategyFactory()
                : new KSeriesStrategyFactory()).getTrainSeatStrategy();
        seat = seatStrategy.allocSeat(startStationIndex, endStationIndex, seatType, train.getSeats());
        if (seat == null) {
            throw new BizException(BizError.OUT_OF_SEAT);
        }
        OrderEntity order = OrderEntity.builder().trainId(trainId).userId(userId).seat(seat)
                .status(OrderStatus.PENDING_PAYMENT).arrivalStationId(toStationId).departureStationId(fromStationId)
                .price(getPrice(trainId, fromStationId, toStationId, seat)).payment(Payment.ALIPAY_PAY.toInteger())
                .stamp(new Date().getTime() + userId.toString() + trainId)
                .build();
        train.setUpdatedAt(null);// force it to update
        trainDao.saveAndFlush(train);
        orderDao.saveAndFlush(order);
        return order.getId();
    }

    public List<OrderVO> listOrders(String username) {
        Long userId = userDao.findByUsername(username).getId();
        List<OrderEntity> orders = orderDao.findByUserId(userId);
        orders.sort((o1, o2) -> {
            return o2.getId().compareTo(o1.getId());
        });
        return orders.stream().map(order -> {
            TrainEntity train = trainDao.findById(order.getTrainId()).get();
            RouteEntity route = routeDao.findById(train.getRouteId()).get();
            int startIndex = route.getStationIds().indexOf(order.getDepartureStationId());
            int endIndex = route.getStationIds().indexOf(order.getArrivalStationId());
            return OrderVO.builder().id(order.getId()).trainId(order.getTrainId())
                    .seat(order.getSeat()).status(order.getStatus().getText())
                    .createdAt(order.getCreatedAt())
                    .startStationId(order.getDepartureStationId())
                    .endStationId(order.getArrivalStationId())
                    .departureTime(train.getDepartureTimes().get(startIndex))
                    .arrivalTime(train.getArrivalTimes().get(endIndex))
                    .build();
        }).collect(Collectors.toList());
    }

    public OrderVO getOrder(Long id) {
        OrderEntity order = orderDao.findById(id).get();
        TrainEntity train = trainDao.findById(order.getTrainId()).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startIndex = route.getStationIds().indexOf(order.getDepartureStationId());
        Date departureDate = train.getDepartureTimes().get(startIndex);
        UserEntity user = userDao.findById(order.getUserId()).get();
        Date stopCancel = new Date(departureDate.getTime() - 60 * 60 * 1000);
        PaymentStrategy paymentStrategy = order.getPayment().equals(Payment.WECHAT_PAY.toInteger())
                ? new WeChatPaymentStrategy()
                : new AlipayPaymentStrategy();
        OrderStatus getStatus = order.getStatus();
        if (getStatus == null || getStatus.equals(OrderStatus.PENDING_PAYMENT)) {
            try {
                getStatus = paymentStrategy.checkOrderStatus(order.getStamp());
                if (getStatus.equals(OrderStatus.PAID)) {
                    order.setStatus(OrderStatus.PAID);
                    order.setUpdatedAt(null);
                    orderDao.save(order);
                } else if (getStatus.equals(OrderStatus.CANCELLED)) {
                    cancelOrder(id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (order.getStatus().equals(OrderStatus.PAID) && (new Date().after(stopCancel))) {
            order.setStatus(OrderStatus.COMPLETED);
            user.setCredit(new CreditStrategy().getNewCredit(user.getCredit(), order.getPrice()));
            userDao.saveAndFlush(user);
            orderDao.saveAndFlush(order);
        }
        int endIndex = route.getStationIds().indexOf(order.getArrivalStationId());
        return OrderVO.builder().id(order.getId()).trainId(order.getTrainId())
                .seat(order.getSeat()).status(order.getStatus().getText())
                .createdAt(order.getCreatedAt())
                .startStationId(order.getDepartureStationId())
                .endStationId(order.getArrivalStationId())
                .departureTime(train.getDepartureTimes().get(startIndex))
                .arrivalTime(train.getArrivalTimes().get(endIndex))
                .price(order.getPrice())
                .build();
    }

    public void cancelOrder(Long id) {
        OrderEntity order = orderDao.findById(id).get();

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }

        TrainEntity train = trainDao.findById(order.getTrainId()).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startStationIndex = 0;
        int endStationIndex = 0;

        for (int i = 0; i < route.getStationIds().size(); i++) {
            if (Objects.equals(route.getStationIds().get(i), order.getDepartureStationId())) {
                startStationIndex = i;
            }
            if (Objects.equals(route.getStationIds().get(i), order.getArrivalStationId())) {
                endStationIndex = i;
            }
        }

        if (order.getStatus() == OrderStatus.PAID) {
            int moneyToRefund = order.getPrice();
            PaymentStrategy paymentStrategy = order.getPayment().equals(Payment.WECHAT_PAY.toInteger())
                    ? new WeChatPaymentStrategy()
                    : new AlipayPaymentStrategy();
            try {
                if (!paymentStrategy.refundOrder(moneyToRefund, order.getStamp())) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        int seatId = -1;
        if (train.getTrainType() == TrainType.HIGH_SPEED) {
            GSeriesStrategyFactory gSeriesStrategyFactory = new GSeriesStrategyFactory();
            seatId = gSeriesStrategyFactory.getTrainSeatStrategy().findSeatIdByDescription(order.getSeat()).intValue();
        } else if (train.getTrainType() == TrainType.NORMAL_SPEED) {
            KSeriesStrategyFactory kSeriesStrategyFactory = new KSeriesStrategyFactory();
            seatId = kSeriesStrategyFactory.getTrainSeatStrategy().findSeatIdByDescription(order.getSeat()).intValue();
        }

        TrainSeatStrategy.deallocSeatById(startStationIndex, endStationIndex, seatId, train.getSeats());
        train.setUpdatedAt(null);
        trainDao.saveAndFlush(train);

        order.setStatus(OrderStatus.CANCELLED);
        orderDao.saveAndFlush(order);
    }

    public String payOrder(Long id, boolean useCredit, Payment payment) {
        OrderEntity order = orderDao.findById(id).get();

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }
        order.setPayment(payment.toInteger());

        int moneyToPay = getPriceBothWay(id, useCredit);
        UserEntity user = userDao.findById(order.getUserId()).get();

        if (useCredit) {
            user.setCredit(0);
            userDao.saveAndFlush(user);
        }
        order.setPrice(moneyToPay);
        orderDao.saveAndFlush(order);

        PaymentStrategy paymentStrategy = payment.equals(Payment.WECHAT_PAY) ? new WeChatPaymentStrategy()
                : new AlipayPaymentStrategy();

        try {
            return paymentStrategy.PayOrder(moneyToPay, order.getId().toString(), order.getStamp());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getPrice(Long trainId, Long departureStationId, Long arrivalStationId, String seatDescription) {
        int price = 0;
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startStationIndex = 0;
        int endStationIndex = 0;

        for (int i = 0; i < route.getStationIds().size(); i++) {
            if (Objects.equals(route.getStationIds().get(i), departureStationId)) {
                startStationIndex = i;
            }
            if (Objects.equals(route.getStationIds().get(i), arrivalStationId)) {
                endStationIndex = i;
            }
        }
        TrainStrategyFactory trainStrategyFactory = train.getTrainType().equals(TrainType.HIGH_SPEED)
                ? new GSeriesStrategyFactory()
                : new KSeriesStrategyFactory();
        String seatType = trainStrategyFactory.getTrainSeatStrategy().findSeatTypeByDescription(seatDescription);
        price = trainStrategyFactory.getTicketPriceStrategy().getPrice(startStationIndex, endStationIndex, seatType);
        return price;
    }

    @Override
    public OrderStatus getStatus(Long id) {
        return orderDao.findById(id).get().getStatus();
    }

    @Override
    public int getPriceBothWay(Long id, boolean ifCredit) {
        OrderEntity order = orderDao.findById(id).get();
        UserEntity user = userDao.findById(order.getUserId()).get();
        int moneyToPay = order.getPrice();
        if (ifCredit) {
            CreditStrategy creditStrategy = new CreditStrategy();

            moneyToPay -= creditStrategy.getReducedMoney(user.getCredit());
            if (moneyToPay < 0) {
                moneyToPay = 0;
            }
        }
        return moneyToPay;
    }
}
