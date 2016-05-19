package com.yumu.hexie.model;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.yumu.hexie.model.redis.Keys;
import com.yumu.hexie.model.system.SystemConfig;

@Component(value = "multipleRepository")
public class MultipleRepository {

    private static final Logger SCHEDULE_LOG = LoggerFactory.getLogger("com.yumu.hexie.schedule");

    @Inject
    @Named("mainRedisTemplate")
    private RedisTemplate<String, SystemConfig> mainRedisTemplate;
    
    @Inject
    @Named("baofangRedisTemplate")
    private RedisTemplate<String, SystemConfig> baofangRedisTemplate;
    
    @Inject
    @Named("chunhuiRedisTemplate")
    private RedisTemplate<String, SystemConfig> chunhuiRedisTemplate;
    
    public void setSystemConfig(String key,SystemConfig value) {

        SCHEDULE_LOG.warn("update cache:" + key + "["+value+"]");
        mainRedisTemplate.opsForValue().set(Keys.systemConfigKey(key), value, 5, TimeUnit.MINUTES);

        SystemConfig c = mainRedisTemplate.opsForValue().get(Keys.systemConfigKey(key));
        if(c != null) {
            SCHEDULE_LOG.warn("get mainRedis cache:"+c.getSysKey() + "["+c.getSysValue()+"]");
        }
        baofangRedisTemplate.opsForValue().set(Keys.systemConfigKey(key), value, 5, TimeUnit.MINUTES);
        
        c = baofangRedisTemplate.opsForValue().get(Keys.systemConfigKey(key));
        if(c != null) {
            SCHEDULE_LOG.warn("get baofangRedis cache:"+c.getSysKey() + "["+c.getSysValue()+"]");
        }
        chunhuiRedisTemplate.opsForValue().set(Keys.systemConfigKey(key), value, 5, TimeUnit.MINUTES);
        
        c = chunhuiRedisTemplate.opsForValue().get(Keys.systemConfigKey(key));
        if(c != null) {
            SCHEDULE_LOG.warn("get chunhuiRedis cache:"+c.getSysKey() + "["+c.getSysValue()+"]");
        }
        
        SCHEDULE_LOG.warn("END update cache:" + key + "["+value+"]");
    }
    
    /**
     * 为其他公众号SET ACCESS_TOKEN,除了合协以外的
     */
    public void setOtherAccessToken(String key,SystemConfig value){
    	
        SCHEDULE_LOG.warn("BEGIN set other cache:" + key + "["+value+"]");

        chunhuiRedisTemplate.opsForValue().set(Keys.systemConfigKey(key), value, 5, TimeUnit.MINUTES);
        SCHEDULE_LOG.warn("set chunhuiRedis cache:"+Keys.systemConfigKey(key) + "["+value+"]");
        
        SCHEDULE_LOG.warn("END set other cache:" + key + "["+value+"]");
    	
    }

}
