package org.fffd.l23o6.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.fffd.l23o6.dao.StationDao;
import org.fffd.l23o6.mapper.StationMapper;
import org.fffd.l23o6.pojo.entity.StationEntity;
import org.fffd.l23o6.pojo.vo.station.StationVO;
import org.fffd.l23o6.service.StationService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService{
    private final StationDao stationDao;
    @Override
    public StationVO getStation(Long stationId){
        return StationMapper.INSTANCE.toStationVO(stationDao.findById(stationId).get());
    }
    @Override
    public List<StationVO> listStations(){
        return stationDao.findAll().stream().map(StationMapper.INSTANCE::toStationVO).collect(Collectors.toList());
    }
    @Override
    public void addStation(String name){
        stationDao.save(StationEntity.builder().name(name).build());
    }
}