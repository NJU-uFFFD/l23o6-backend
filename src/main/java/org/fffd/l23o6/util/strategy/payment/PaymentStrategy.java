package org.fffd.l23o6.util.strategy.payment;
import org.fffd.l23o6.pojo.enum_.OrderStatus;

public abstract class PaymentStrategy {
    public abstract String PayOrder(int money, String id, String stamp) throws Exception;

    public abstract OrderStatus checkOrderStatus(String out_trade_no) throws Exception;

    public abstract boolean refundOrder(int money, String id) throws Exception;
}
