package com.imooc.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: xiaoyu
 * Date: 2020/10/21
 * Time: 9:58
 * Description: Be brave or be a loser
 */
@Component
@ConfigurationProperties(prefix = "wx")
@Data
public class WxAccountConfig {
    private String appId;
    private String mchId;
    private String mchKey;
    private String notifyUrl;
    private String returnUrl;
}