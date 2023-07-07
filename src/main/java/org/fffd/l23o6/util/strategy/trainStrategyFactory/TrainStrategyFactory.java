package org.fffd.l23o6.util.strategy.trainStrategyFactory;

import org.fffd.l23o6.util.strategy.ticketPrice.TicketPriceStrategy;
import org.fffd.l23o6.util.strategy.trainSeat.TrainSeatStrategy;

public interface TrainStrategyFactory {
    TrainSeatStrategy getTrainSeatStrategy();
    TicketPriceStrategy getTicketPriceStrategy();
}
