/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.service.common;

import java.util.Set;

import com.yumu.hexie.integration.wechat.entity.AccessToken;

/**
 * <pre>
 * 系统参数统一获取
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: SystemConfigService.java, v 0.1 2016年3月30日 上午11:51:34  Exp $
 */
public interface SystemConfigService {

    public int querySmsChannel();
    
    
    public String[] queryActPeriod();
    public Set<String> getUnCouponItems();

    public String queryJsTickets();
    public String queryWXAToken();

    public String querySecret(String appId);
    public String[] queryAppIds();
    
    
    public void saveAccessToken(String appId, AccessToken at);
    public AccessToken queryWXAccToken(String appId);
    
    public String queryValueByKey(String key);
}
