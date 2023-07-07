package org.fffd.l23o6.util.strategy.payment;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import org.fffd.l23o6.pojo.enum_.OrderStatus;

import java.util.HashMap;
import java.util.Map;

public class AlipayPaymentStrategy extends PaymentStrategy {
    private final AlipayClient alipayClient = new DefaultAlipayClient(
            "https://openapi-sandbox.dl.alipaydev.com/gateway.do",
            "9021000123600568",
            "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCxaz1yclzQqlEZO5GigHABYZJArXfVDLeQ0jkQV9z/dQKqDF7r+QiLKriv7lNO4Er2Vq7IxznPp5bFpNUerr1rBUyF5Vvvx6B29QvmKXDtsHwpNqLifoAhs4aycVA/REDaq5Lh3l5jI2Rny+EMt+RbAG0G2UWWsaq7evwHVHkU1Tv5hvNhY1+88V5GS/YE5o+dlBPUA9ZiwiadhDZY4oF3b/RCWV82AwiIZDqQDEWxMJITs8gEmE7QUcLonBjSnYdChnxehdBLLkgflUAuhM9vzSTmdAgEfIEze5XnJhXtKXsr0XP7/aSSRawHls8k4OOJIcsnE+3cQ8ws5VNj48Y1AgMBAAECggEAWWScUFb5FxZJyHnwNj2ascd0xmvg7hL7Rtf1cpuiuYA+eGnFRgZzXJOI/PQLO7nda2Nfq8BeC5f4attTmqMWw56m4Owgt58Q5f8wIKcOxGRhGu7xgUeVzeVD4BMiCM6eEs/SMUY2rqL9iyL54MujHosHZCUDYBti2BXueM8CNxEmEc3Kz1VI+Czkzu/nADNUm9zTodbr/mTGNSGqGWtVDKv6IjJixlSrIMYkBgimDNG8Sr6J812pdSOiGagHlELOf8fQ3aOHecfRtzz0R6h0KuQyVjI2sMrk8Blr4oppjGyZpVu7lndVasn9cbKLLbBMNlFh3iLdKd1ShUSCzPn9gQKBgQDom87tjed+PUndtUSL+1SfPPYWHy7xuSwFtKV0to72aQHOMxV1o8An8d0wO6SL289NnGTIAgXF0lTB75P3C68vcq+iw4AEXAC49ITl0tfgIqjvN/B3syHTgMtQZVEkgh5CNWlsZG3WlA0neULBkfPCgK+8ADHkBGwWMU+peo6PYQKBgQDDQqXOmmKM9dQyQiXn5JpYJSC0Wnxb/RmF/0DFcKLlRhoOqSqtgY0FH23oaRl6U/d9UfkiVuidNG80xBSIyrwNsSrYSrW/7EyotHlpqjJEHND/sm63v2f78A5BxUsB4NYZ0gslSnZaiDD+9QUZ4CWLuFcz3UgNmvMZFIhKNYULVQKBgGyAUTPKH5NpL011wnWxVLW6j+edwB4NDBXVP+CDT4htTuRKO9ZV/cokLdLT31tFuxsuumv5VYwDInocMN7p2lu9mWPI4awm5kdf96XkhcINKNP9OfP74OlxiGUF/aWT9F1Z4MUoy3tuL/ybuaHlXNj7gf2l8Z+02F45vjkF6/3BAoGBALIH0Hnhlb2EXmc8nKckbf20TG9kmUKeMGlWZE4g9sUo95UqdLkiB9wF3gYr3YkFif//f7Hpn8nGBycLKNXYU0IAqVnwNGq7HuIrOxA2mAI2a/DXVc+/eyXgqH8bTdp2j3+XXYYwsFy3FEVCTdZb4p/UAd5OAeY1wzS43dMbnSTJAoGBAOGC+df01At9+Do+AMCZbXXFaRWYNhsHy5Ij+EqYtw5CCMesegPePsDDt4e90+eepgbhv8LDF11wlfATRqhUXG/AQLyR4WRXGqSKsdxzSCI27pOyGsM6U7xQLX+DGEpMntEEr8WiHPo0OMeR7JJ/SAcKS2UgCF9DCFcugJtbCo3c",
            "json", "utf-8",
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzpUepmxcYw8Fa5EhZ+54HQ9YHHt3KNcGODIILKaRMFicw9ZJ8s+x53hkMQ/1DpccFf5t1MiSOv3BRgDeOjwKhpGoL42wGITmoq2N/Z1ftnE/Y045Q8o7DBD2So+LwU+dSEJ6v48mI+wHh3LphhPUa4heYMSaSr70AJrKAt+9qgN4xl+rs62Gdj2vCaJmCVkfLFlK2jbEQt+XsykRsViMVFIeLAeHjZV/zNRwlIm7zqozVjNa5So7SvAX6W/5Pp+V9Da2x5X4S4Pb1QB6XTLlDdmFidpMqDWDfnsPxOHu2jNulfUFbtvZbLS9LOtzWtGP306hiN9wYpQW8YcYtVqURQIDAQAB",
            "RSA2");

    private final Map<String, OrderStatus> revertToOrderStatus = new HashMap<>();

    public AlipayPaymentStrategy() {
        revertToOrderStatus.put("WAIT_BUYER_PAY", OrderStatus.PENDING_PAYMENT);
        revertToOrderStatus.put("TRADE_SUCCESS", OrderStatus.PAID);
        revertToOrderStatus.put("TRADE_CLOSED", OrderStatus.CANCELLED);
    }

    @Override
    public String PayOrder(int money, String id) throws AlipayApiException {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl("");
        request.setReturnUrl("http://localhost:5173/user");
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", id);
        bizContent.put("total_amount", money);
        bizContent.put("subject", "train ticket");
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        bizContent.put("timeout_express", "1m");

        request.setBizContent(bizContent.toString());
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        System.out.println(response.getBody());
        return response.getBody();
    }

    @Override
    public OrderStatus checkOrderStatus(String out_trade_no) throws AlipayApiException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", out_trade_no);
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        System.out.println(response.getBody());
        if ("ACQ.TRADE_NOT_EXIST".equals(response.getSubCode())) {
            return OrderStatus.PENDING_PAYMENT;
        }
        return revertToOrderStatus.getOrDefault(response.getTradeStatus(), OrderStatus.PENDING_PAYMENT);
    }

    @Override
    public boolean refundOrder(int money, String id) throws AlipayApiException {

        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        JSONObject bizContent = new JSONObject();
        //bizContent.put("trade_no", id);
        bizContent.put("out_trade_no", id);
        bizContent.put("refund_amount", money);

        request.setBizContent(bizContent.toString());
        AlipayTradeRefundResponse response = alipayClient.execute(request);
        if (response.isSuccess()) {
            System.out.println("调用成功");
            return true;
        } else {
            System.out.println("调用失败");
            return false;
        }
    }
}
