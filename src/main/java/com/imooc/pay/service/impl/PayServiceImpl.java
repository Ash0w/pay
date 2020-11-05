package com.imooc.pay.service.impl;

import com.imooc.pay.dao.PayInfoMapper;
import com.imooc.pay.enums.PayPlatfromEnum;
import com.imooc.pay.pojo.PayInfo;
import com.imooc.pay.service.IPayService;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Repository
@Service
public class PayServiceImpl implements IPayService {
    @Autowired
    private BestPayService bestPayService;
    @Autowired
    private PayInfoMapper payInfoMapper;

    /**
     * 创建、发起支付
     *
     * @param orderId
     * @param amount
     * @return
     */
    @Override
    public PayResponse create(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum) {
        //写入数据库
        PayInfo payInfo = new PayInfo(Long.parseLong(orderId),
                PayPlatfromEnum.getByBestPayTypeEnum(bestPayTypeEnum).getCode(),
                OrderStatusEnum.NOTPAY.name(), amount
        );
        payInfoMapper.insertSelective(payInfo);
        //发起支付
        PayRequest request = new PayRequest();
        request.setOrderName("5990545-最好的支付sdk");
        request.setOrderId(orderId);
        request.setOrderAmount(amount.doubleValue());
        request.setPayTypeEnum(bestPayTypeEnum);
        //封装获得支付平台回调信息
        PayResponse response = bestPayService.pay(request);
        log.info("发起支付 response={}", response);
        return response;
    }

    /**
     * 异步通知处理
     *
     * @param notifyData
     * @return
     */
    @Override
    public String asyncNotify(String notifyData) {
        //1.签名校验
        PayResponse response = bestPayService.asyncNotify(notifyData);
        log.info("异步通知 response={}", response);
        //2.金额校验（从数据库查订单）
        //比较严重(正常情况不会发生)发出告警：钉钉、短信
        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(response.getOrderId()));
        if (payInfo == null) {
            //告警
            throw new RuntimeException("通过OrderNo查到的结果是null");
        }
        //如果查询出来的订单状态不是“已支付”
        if (!payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.name())) {
            //Double类型相比较不好控制精度。1.00和1.0 所以比较BigDecimal
            if (payInfo.getPayAmount().compareTo(BigDecimal.valueOf(response.getOrderAmount())) != 0) {
                //告警
                throw new RuntimeException("异步通知中的金额和数据库里的不一致，orderNo:" + response.getOrderId());
            }
            //3.修改订单支付状态
            payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
            payInfo.setPlatformNumber(response.getOutTradeNo());
            //更新时间设置为空，因为mysql拥有时间处理功能，所有不需要我们处理
            payInfo.setUpdateTime(null);
            payInfoMapper.updateByPrimaryKeySelective(payInfo);
        }
        //TODO pay发送MQ消息，mall接收MQ消息

        //4.告知支付平台不再通知
        if (response.getPayPlatformEnum() == BestPayPlatformEnum.WX) {
            return "<xml>\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";
        } else if (response.getPayPlatformEnum() == BestPayPlatformEnum.ALIPAY) {
            return "success";
        }
        throw new RuntimeException("异步通知中错误的支付平台");
    }

    @Override
    public PayInfo queryByOderId(String orderId) {
        return payInfoMapper.selectByOrderNo(Long.parseLong(orderId));
    }
}
