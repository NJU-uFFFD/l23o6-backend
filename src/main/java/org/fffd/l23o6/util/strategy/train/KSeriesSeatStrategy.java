package org.fffd.l23o6.util.strategy.train;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Nullable;


public class KSeriesSeatStrategy extends TrainSeatStrategy {
    public static final KSeriesSeatStrategy INSTANCE = new KSeriesSeatStrategy();
     
    private final Map<Integer, String> SOFT_SLEEPER_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> HARD_SLEEPER_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> SOFT_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> HARD_SEAT_MAP = new HashMap<>();

    private final Map<KSeriesSeatType, Map<Integer, String>> TYPE_MAP = new HashMap<>() {{
        put(KSeriesSeatType.SOFT_SLEEPER_SEAT, SOFT_SLEEPER_SEAT_MAP);
        put(KSeriesSeatType.HARD_SLEEPER_SEAT, HARD_SLEEPER_SEAT_MAP);
        put(KSeriesSeatType.SOFT_SEAT, SOFT_SEAT_MAP);
        put(KSeriesSeatType.HARD_SEAT, HARD_SEAT_MAP);
    }};


    private KSeriesSeatStrategy() {

        int counter = 0;

        for (String s : Arrays.asList("软卧1号上铺", "软卧2号下铺", "软卧3号上铺", "软卧4号上铺", "软卧5号上铺", "软卧6号下铺", "软卧7号上铺", "软卧8号上铺")) {
            SOFT_SLEEPER_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("硬卧1号上铺", "硬卧2号中铺", "硬卧3号下铺", "硬卧4号上铺", "硬卧5号中铺", "硬卧6号下铺", "硬卧7号上铺", "硬卧8号中铺", "硬卧9号下铺", "硬卧10号上铺", "硬卧11号中铺", "硬卧12号下铺")) {
            HARD_SLEEPER_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("1车1座", "1车2座", "1车3座", "1车4座", "1车5座", "1车6座", "1车7座", "1车8座", "2车1座", "2车2座", "2车3座", "2车4座", "2车5座", "2车6座", "2车7座", "2车8座")) {
            SOFT_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("3车1座", "3车2座", "3车3座", "3车4座", "3车5座", "3车6座", "3车7座", "3车8座", "3车9座", "3车10座", "4车1座", "4车2座", "4车3座", "4车4座", "4车5座", "4车6座", "4车7座", "4车8座", "4车9座", "4车10座")) {
            HARD_SEAT_MAP.put(counter++, s);
        }
    }

    public enum KSeriesSeatType implements SeatType {
        SOFT_SLEEPER_SEAT("软卧"), HARD_SLEEPER_SEAT("硬卧"), SOFT_SEAT("软座"), HARD_SEAT("硬座"), NO_SEAT("无座");
        private String text;
        KSeriesSeatType(String text){
            this.text=text;
        }
        public String getText() {
            return this.text;
        }
        public static KSeriesSeatType fromString(String text) {
            for (KSeriesSeatType b : KSeriesSeatType.values()) {
                if (b.text.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }


    public @Nullable String allocSeat(int startStationIndex, int endStationIndex, KSeriesSeatType type, boolean[][] seatMap) {
        //endStationIndex - 1 = upper bound
        int maxScore = -1;
        String result = null;
        int resultIndex = -1;
        Map<Integer,String> map = TYPE_MAP.get(type);
        if(map == null){
            if(type == KSeriesSeatType.NO_SEAT){
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

    public Map<KSeriesSeatType, Integer> getLeftSeatCount(int startStationIndex, int endStationIndex, boolean[][] seatMap) {
        Map<KSeriesSeatType, Integer> map = new HashMap<>();

        for (Map.Entry<KSeriesSeatType, Map<Integer, String>> entry : TYPE_MAP.entrySet()) {
            
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
        return new boolean[stationCount - 1][SOFT_SLEEPER_SEAT_MAP.size() + HARD_SLEEPER_SEAT_MAP.size() + SOFT_SEAT_MAP.size() + HARD_SEAT_MAP.size()];
    }
}
