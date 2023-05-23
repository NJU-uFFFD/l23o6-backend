package org.fffd.l23o6.pojo.vo.order;

import java.util.Date;

import lombok.Data;


@Data
public class OrderVO {
    private Long id;
    private Long trainId;
    private Long departureStationId;
    private Long arrivalStationId;
    private String status;
    private Date createdAt;
    private String seat;
}
