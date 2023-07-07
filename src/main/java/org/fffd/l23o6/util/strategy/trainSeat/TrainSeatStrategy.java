package org.fffd.l23o6.util.strategy.trainSeat;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import jakarta.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class TrainSeatStrategy {

    protected final Map<String, Integer> DESCRIPTION_ID_MAP = new HashMap<>();
    protected final Map<Integer, SeatType> SEATID_TYPE_MAP = new HashMap<>();

    public interface SeatType {
        public String getText();
    }

    public static void deallocSeatById(int startStationIdx, int endStationIdx, int seatId, boolean seats[][]) {
        for (int i = startStationIdx; i < endStationIdx; i++) {
            seats[i][seatId] = false;
        }
    }

    public abstract boolean[][] initSeatMap(int stationCount);

    public abstract @Nullable String allocSeat(int startStationIndex, int endStationIndex, String type,
            boolean[][] seatMap);

    public static void allocSeatById(int startStationIndex, int endStationIndex, int seatId, boolean[][] seatMap) {
        for (int i = startStationIndex; i < endStationIndex; i++) {
            seatMap[i][seatId] = true;
        }
    }

    public abstract Map<SeatType, Integer> getLeftSeatCount(int startStationIndex, int endStationIndex,
            boolean[][] seatMap);

    public static boolean judgeIfSeatFree(int startStationIdx, int endStationIdx, int seatId, boolean[][] seatMap) {
        for (int i = startStationIdx; i < endStationIdx; i++) {
            if (seatMap[i][seatId]) {
                return false;
            }
        }
        return true;
    }

    public static @Nullable String allocSeatFreely(int startStationIndex, int endStationIndex,
            Map<Integer, String> mapHere,
            boolean[][] seatMap) {
        for (Map.Entry<Integer, String> entry : mapHere.entrySet()) {
            int seatNum = entry.getKey();
            if (judgeIfSeatFree(startStationIndex, endStationIndex, seatNum, seatMap)) {
                allocSeatById(startStationIndex, endStationIndex, seatNum, seatMap);
                return entry.getValue();
            }
        }
        return null;
    }

    public void deallocSeatByDescription(int startStationIdx, int endStationIdx, String description,
            boolean[][] seatMap) {
        Integer getInt = DESCRIPTION_ID_MAP.get(description);
        if (getInt == null) {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "座位描述有误");
        }
        int seatId = getInt;
        deallocSeatById(startStationIdx, endStationIdx, seatId, seatMap);
    }

    public Integer findSeatIdByDescription(String description) {
        Integer seatId = DESCRIPTION_ID_MAP.get(description);
        return seatId;
    }

    public String findSeatTypeByDescription(String description) {
        Integer seatId = DESCRIPTION_ID_MAP.get(description);
        String seatType = SEATID_TYPE_MAP.get(seatId).getText();
        return seatType;
    }

}
