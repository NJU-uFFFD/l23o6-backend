package org.fffd.l23o6.util.strategy.trainStrategyFactory;

import org.fffd.l23o6.util.strategy.ticketPrice.GSeriesTicketPriceStrategy;
import org.fffd.l23o6.util.strategy.ticketPrice.TicketPriceStrategy;
import org.fffd.l23o6.util.strategy.trainSeat.GSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.trainSeat.TrainSeatStrategy;

public class GSeriesStrategyFactory implements TrainStrategyFactory{
    @Override
    public TrainSeatStrategy getTrainSeatStrategy() {
        return GSeriesSeatStrategy.INSTANCE;
    }

    @Override
    public TicketPriceStrategy getTicketPriceStrategy() {
        return GSeriesTicketPriceStrategy.INSTANCE;
    }
}
