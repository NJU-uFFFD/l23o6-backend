package org.fffd.l23o6.service;

public interface OrderService {
    void createOrder(Long userId, Long trainId, Long fromStationId, Long toStationId, Long seatTypeId, Long seatNumber);
}
