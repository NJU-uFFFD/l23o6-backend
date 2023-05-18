package org.fffd.l23o6.mapper;

import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.StationEntity;
import org.fffd.l23o6.pojo.vo.route.RouteVO;
import org.fffd.l23o6.pojo.vo.station.StationVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RouteMapper {
    RouteMapper INSTANCE = Mappers.getMapper(RouteMapper.class);

    RouteVO toStationVO(RouteEntity routeEntity);
}
