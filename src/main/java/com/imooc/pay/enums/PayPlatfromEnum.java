package com.imooc.pay.enums;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import lombok.Getter;

@Getter
public enum PayPlatfromEnum {
    //1-支付宝,2-微信
    ALIPAY(1),
    WX(2),
    ;
    Integer code;

    PayPlatfromEnum(Integer code) {
        this.code = code;
    }

    public static PayPlatfromEnum getByBestPayTypeEnum(BestPayTypeEnum bestPayTypeEnum) {
        //如果遍历到1或者2，则返回当前的值，否则打印错误的支付平台信息
        for (PayPlatfromEnum payPlatfromEnum : PayPlatfromEnum.values()) {
            if (bestPayTypeEnum.getPlatform().name().equals(payPlatfromEnum.name())) {
                return payPlatfromEnum;
            }
        }
        throw new RuntimeException("错误的支付平台:" + bestPayTypeEnum.name());
    }
}
