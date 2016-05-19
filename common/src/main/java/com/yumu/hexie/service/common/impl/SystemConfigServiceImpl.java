/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.service.common.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.json.JSONException;
import org.springframework.stereotype.Service;

import com.yumu.hexie.common.util.JacksonJsonUtil;
import com.yumu.hexie.integration.wechat.entity.AccessToken;
import com.yumu.hexie.model.redis.RedisRepository;
import com.yumu.hexie.model.system.SystemConfig;
import com.yumu.hexie.model.system.SystemConfigRepository;
import com.yumu.hexie.service.common.SystemConfigService;
import com.yumu.hexie.service.exception.BizValidateException;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: SystemConfigServiceImpl.java, v 0.1 2016年4月7日 下午4:39:59  Exp $
 */
@Service("systemConfigService")
public class SystemConfigServiceImpl implements SystemConfigService {

    public static final String JS_TOKEN = "JS_TOKEN";
    public static final String ACC_TOKEN = "ACCESS_TOKEN";
    
    private static final String NOCOUPON_ITEMS = "NOCOUPON_ITEMS";
    private static final String ACT_PERIOD = "ACT_PERIOD";
    
    private static final String APP_SECRET_KEY = "APPSEC_%s";
    public static final String APP_ACC_TOKEN = "APP_TOKEN_%s";
    private static final String APPIDS = "APPIDS";
    @Inject
    private SystemConfigRepository systemConfigRepository;
    @Inject
    private RedisRepository redisRepository;
    /** 
     * @return
     * @see com.yumu.hexie.service.common.SystemConfigService#querySmsChannel()
     */
    @Override
    public int querySmsChannel() {
        List<SystemConfig> list = systemConfigRepository.findAllBySysKey("SMS_CHANNEL");
        if (list.size()>0) {
            SystemConfig config = list.get(0);
            String setting = config.getSysValue();
            
            if (!"0".equals(setting)) {
                return 1;
            }
        }
        return 0;
    }

    public String[] queryActPeriod() {
        List<SystemConfig> list = systemConfigRepository.findAllBySysKey(ACT_PERIOD);
        if (list.size()>0) {
            
            SystemConfig systemConfig = list.get(0);
            String datePeriod = systemConfig.getSysValue();
            
            return datePeriod.split(",");
        } else {
            return new String[0];
        }
    }
    
    public Set<String> getUnCouponItems() {
        Set<String> res = new HashSet<String>();
        SystemConfig systemConfig = getConfigWithCache(NOCOUPON_ITEMS);
        if(systemConfig != null) {
            String ids = systemConfig.getSysValue();
            for(String idStr: ids.split(",")) {
                res.add(idStr.trim());
            }
        }
        return res;
    }
    private SystemConfig getConfigWithCache(String key) {
        SystemConfig systemConfig = redisRepository.getSystemConfig(key);
        if(systemConfig == null) {
            List<SystemConfig> list = systemConfigRepository.findAllBySysKey(key);
            if (list.size()>0) {
                systemConfig = list.get(0);
                redisRepository.setSystemConfig(key, systemConfig);
            }
        }
        return systemConfig;
    }
    /** 
     * @param appId
     * @return
     * @see com.yumu.hexie.service.common.SystemConfigService#queryWXAccToken(java.lang.String)
     */
    @Override
    public AccessToken queryWXAccToken(String appId) {
        SystemConfig config = getConfigWithCache(String.format(APP_ACC_TOKEN, appId));
        if (config != null) {
            try {
                return (AccessToken) JacksonJsonUtil.jsonToBean(
                        config.getSysValue(), AccessToken.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        throw new BizValidateException("微信token没有记录" + appId);
    }
    /** 
     * @param appId
     * @return
     * @see com.yumu.hexie.service.common.SystemConfigService#querySecret(java.lang.String)
     */
    @Override
    public String querySecret(String appId) {
        SystemConfig systemConfig = getConfigWithCache(String.format(APP_SECRET_KEY, appId));
        if(systemConfig != null) {
            return systemConfig.getSysValue();
        }
        return null;
    }
    /** 
     * @param appId
     * @param at
     * @see com.yumu.hexie.service.common.SystemConfigService#saveAccessToken(java.lang.String, com.yumu.hexie.integration.wechat.entity.AccessToken)
     */
    @Override
    public void saveAccessToken(String appId, AccessToken at) {
        try {
            SystemConfig config = null;
            List<SystemConfig> configs = systemConfigRepository
                    .findAllBySysKey(String.format(APP_ACC_TOKEN, appId));
            if (configs.size() > 0) {
                config = configs.get(0);
                config.setSysValue(JacksonJsonUtil.beanToJson(at));
            } else {
                config = new SystemConfig(String.format(APP_ACC_TOKEN, appId),
                    JacksonJsonUtil.beanToJson(at));
            }
            redisRepository.setSystemConfig(String.format(APP_ACC_TOKEN, appId),config);
            systemConfigRepository.save(config);
        } catch (JSONException e) {
        }
    }
    /** 
     * @return
     * @see com.yumu.hexie.service.common.SystemConfigService#queryAppIds()
     */
    @Override
    public String[] queryAppIds() {
        SystemConfig systemConfig = getConfigWithCache(APPIDS);
        if(systemConfig != null) {
            return systemConfig.getSysValue().split(",");
        }
        return new String[0];
    }

    @Override
    public String queryWXAToken() {
        SystemConfig config = getConfigWithCache(ACC_TOKEN);
        if (config != null) {
            try {
                return ((AccessToken) JacksonJsonUtil.jsonToBean(
                        config.getSysValue(), AccessToken.class)).getToken();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        throw new BizValidateException("微信token没有记录");
    }
    

    @Override
    public String queryJsTickets() {
        String tickets = "";
        SystemConfig config = getConfigWithCache(JS_TOKEN);
        if (config != null) {
            tickets = config.getSysValue();
        }
        return tickets;
    }

	@Override
	public String queryValueByKey(String key) {
		
		String ret = "";
		List<SystemConfig> list = systemConfigRepository.findAllBySysKey(key);
		if (list.size()>0) {
			SystemConfig config = list.get(0);
			ret = config.getSysValue();
		}
	
		return ret;
	}
}
