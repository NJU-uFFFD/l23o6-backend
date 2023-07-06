package org.fffd.l23o6.pojo.enum_;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Payment {
  @JsonProperty
  ALIPAY_PAY(0), WECHAT_PAY(1);

  private final int PAYMENT;

  Payment(int type){
      PAYMENT = type;
  }

  public int toInteger() {
    return PAYMENT;
  }
}
