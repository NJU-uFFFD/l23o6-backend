package org.fffd.l23o6.pojo.vo.train;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminTrainVO {
    private Long id;
    private String name;
    private Long routeId;
    private List<String> stationName;
    private Date departureTime;
    private Date arrivalTime;
    private Boolean isLate;
}
