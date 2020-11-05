package com.imooc.pay.service.impl;

import com.imooc.pay.PayApplicationTests;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class PayServiceImplTest extends PayApplicationTests {
    @Autowired
    private PayServiceImpl payServiceImpl;

    @Test
    public void create() {
        //BigDecimal.valueOf(0.01)=new BigDecimal("0.01")
        payServiceImpl.create("15151515687070770",BigDecimal.valueOf(0.01) , BestPayTypeEnum.WXPAY_NATIVE);
    }
}