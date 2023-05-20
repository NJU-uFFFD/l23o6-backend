package org.fffd.l23o6.service;

import org.fffd.l23o6.pojo.vo.route.RouteVO;

import java.util.List;

public interface RouteService {
    void addRoute(String name, List<Integer> stationIds);
    List<RouteVO> listRoutes();

    void editRoute(Long id, String name, List<Integer> stationIds);

}
