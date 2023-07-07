package org.fffd.l23o6.util.strategy.credit;
public class CreditStrategy {


    private final int moneyToCreditRate = 5;

    private final Integer[] standard = { 1000, 3000, 10000, 50000 };
    private final Double[] rate = { 0.005, 0.01, 0.015, 0.02, 0.025 };
    private final Double[] basic = { 5.0, 25.0, 130.0, 930.0 };

    public CreditStrategy() {

    }

    public int getReducedMoney(int credit) {
        for (int i = 3; i >= 0; i--) {
            if (credit >= standard[i]) {
                return (int) (basic[i] + (credit - standard[i]) * rate[i + 1]);
            }
        }

        return (int) (credit * rate[0]);
    }

    public int getNewCredit(int before, int truePay) {
        return before + truePay * moneyToCreditRate;
    }

    public int getDeltaCredit(int truePay) {
        return truePay * moneyToCreditRate;
    }
}
