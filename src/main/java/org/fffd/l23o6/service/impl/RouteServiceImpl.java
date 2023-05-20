package org.fffd.l23o6.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.mapper.RouteMapper;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.vo.route.RouteVO;
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

    @Override
    public List<RouteVO> listRoutes() {
        return routeDao.findAll().stream().map(RouteMapper.INSTANCE::toStationVO).collect(Collectors.toList());
    }

    @Override
    public void editRoute(Long id, String name, List<Integer> stationIds) {
        if (routeDao.findById(id).isEmpty()) {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "该路线不存在");
        }

        routeDao.save(routeDao.findById(id).get().setStationIds(stationIds).setName(name));
    }
}
