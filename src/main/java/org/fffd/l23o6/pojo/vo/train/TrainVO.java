package org.fffd.l23o6.pojo.vo.train;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrainVO {
    @Data
    @AllArgsConstructor
    public class TicketInfo {
        private String type;
        private Integer count;
        private Integer price;
    }
    private Long trainId;
    private String trainName;
    private String startStation;
    private String endStation;
    private Date departureTime;
    private Date arrivalTime;
    private Boolean isLate;
    private List<TicketInfo> ticketInfo;
}
