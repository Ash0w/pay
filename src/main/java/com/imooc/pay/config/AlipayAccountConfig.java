package com.imooc.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: xiaoyu
 * Date: 2020/10/21
 * Time: 11:01
 * Description: Be brave or be a loser
 */
@Component
@ConfigurationProperties(prefix = "alipay")
@Data
public class AlipayAccountConfig {
    private String appId;
    private String privateKey;
    private String alipayPublicKey;
    private String returnUrl;
    private String notifyUrl;
}
