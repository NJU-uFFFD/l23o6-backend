package org.fffd.l23o6.util.strategy.train;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;


public class GSeriesSeatStrategy extends TrainSeatStrategy {
    public static final GSeriesSeatStrategy INSTANCE = new GSeriesSeatStrategy();
     
    private final Map<Integer, String> BUSINESS_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> FIRST_CLASS_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> SECOND_CLASS_SEAT_MAP = new HashMap<>();

    private final Map<GSeriesSeatType, Map<Integer, String>> TYPE_MAP = new HashMap<>() {{
        put(GSeriesSeatType.BUSINESS_SEAT, BUSINESS_SEAT_MAP);
        put(GSeriesSeatType.FIRST_CLASS_SEAT, FIRST_CLASS_SEAT_MAP);
        put(GSeriesSeatType.SECOND_CLASS_SEAT, SECOND_CLASS_SEAT_MAP);
    }};


    private GSeriesSeatStrategy() {

        int counter = 0;

        for (String s : Arrays.asList("1车1A","1车1C","1车1F")) {
            BUSINESS_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("2车1A","2车1C","2车1D","2车1F","2车2A","2车2C","2车2D","2车2F","3车1A","3车1C","3车1D","3车1F")) {
            FIRST_CLASS_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("4车1A","4车1B","4车1C","4车1D","4车2F","4车2A","4车2B","4车2C","4车2D","4车2F","4车3A","4车3B","4车3C","4车3D","4车3F")) {
            SECOND_CLASS_SEAT_MAP.put(counter++, s);
        }
        
    }

    public enum GSeriesSeatType implements SeatType {
        BUSINESS_SEAT("商务座"), FIRST_CLASS_SEAT("一等座"), SECOND_CLASS_SEAT("二等座"), NO_SEAT("无座");
        private String text;
        GSeriesSeatType(String text){
            this.text=text;
        }
        public String getText() {
            return this.text;
        }
        public static GSeriesSeatType fromString(String text) {
            for (GSeriesSeatType b : GSeriesSeatType.values()) {
                if (b.text.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }


    public @Nullable String allocSeat(int startStationIndex, int endStationIndex, GSeriesSeatType type, boolean[][] seatMap) {
        //endStationIndex - 1 = upper bound
        int maxScore = -1;
        String result = null;
        int resultIndex = -1;
        Map<Integer,String> map = TYPE_MAP.get(type);
        if(map == null){
            if(type == GSeriesSeatType.NO_SEAT){
                return "无座";
            }
            else throw new RuntimeException("内部错误：未知的座位类型");
        }
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            int seatNo = entry.getKey();
            boolean available = true;
            for(int i = startStationIndex; i <= endStationIndex - 1; i++){
                if(seatMap[i][seatNo]){
                    available = false;
                    break;
                }
            }
            if(available){
                int score = 0;
                for(int i=startStationIndex-1;i>=0;i--){
                    if(seatMap[i][seatNo]){
                        score += seatMap.length - (startStationIndex - i);
                        break;
                    }
                }
                for(int i=endStationIndex;i<seatMap.length;i++){
                    if(seatMap[i][seatNo]){
                        score += seatMap.length - (i - endStationIndex);
                        break;
                    }
                }
                if(score > maxScore){
                    maxScore = score;
                    result = entry.getValue();
                    resultIndex = seatNo;
                }
            }
        }
        if(resultIndex != -1){
            for(int i=startStationIndex; i<=endStationIndex-1; i++){
                seatMap[i][resultIndex] = true;
            }
        }
        return result;
    }

    public Map<GSeriesSeatType, Integer> getLeftSeatCount(int startStationIndex, int endStationIndex, boolean[][] seatMap) {
        Map<GSeriesSeatType, Integer> map = new HashMap<>();

        for (Map.Entry<GSeriesSeatType, Map<Integer, String>> entry : TYPE_MAP.entrySet()) {
            
            int startTypeIndex = Collections.min(entry.getValue().keySet());
            int endTypeIndex = Collections.max(entry.getValue().keySet());

            int count = 0;
            for (int i = startTypeIndex; i <= endTypeIndex; i++) {
                boolean isFree = true;

                for (int j = startStationIndex; j < endStationIndex; j++) {
                    if (seatMap[i][j]) {
                        isFree = false;
                        break;
                    }  
                }

                if (isFree) count++;
            }
            

            map.put(entry.getKey(), count);
        }

        return map;
    }

    public boolean[][] initSeatMap(int stationCount) {
        return new boolean[stationCount - 1][BUSINESS_SEAT_MAP.size() + FIRST_CLASS_SEAT_MAP.size() + SECOND_CLASS_SEAT_MAP.size()];
    }
}
