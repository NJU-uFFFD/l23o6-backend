package org.fffd.l23o6.service.impl;

import java.util.List;

import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.service.RouteService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {
    private final RouteDao routeDao;
    @Override
    public void addRoute(String name, List<Integer> stationIds) {
        RouteEntity route = RouteEntity.builder().name(name).stationIds(stationIds).build();
        routeDao.save(route);
    }
    
}
