package org.fffd.l23o6.pojo.vo.train;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainVO {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class TicketInfo {
        private String type;
        private Integer count;
        private Integer price;
    }
    private Long id;
    private String name;
    private Long startStationId;
    private Long endStationId;
    private Date departureTime;
    private Date arrivalTime;
    private List<TicketInfo> ticketInfo;
}
