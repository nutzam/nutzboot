package org.nutz.boot.starter.sentinel;

import com.alibaba.csp.sentinel.datasource.AbstractDataSource;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import org.nutz.integration.jedis.RedisService;
import org.nutz.integration.jedis.pubsub.PubSub;
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class SentinelReadableDataSource<T> extends AbstractDataSource<String, T> implements PubSub {
    private static final Log log = Logs.get();

    private PubSubService pubSubService;
    private RedisService redisService;
    private String ruleKey;

    public SentinelReadableDataSource(RedisService redisService, PubSubService pubSubService, String ruleKey, String channel,
                                      Converter<String, T> parser) {
        super(parser);
        AssertUtil.notEmpty(ruleKey, "Redis ruleKey can not be empty");
        AssertUtil.notEmpty(channel, "Redis subscribe channel can not be empty");
        this.pubSubService = pubSubService;
        this.redisService = redisService;
        this.ruleKey = ruleKey;
        loadInitialConfig();
        subscribeFromChannel(channel);
    }

    private void subscribeFromChannel(String channel) {
        log.debugf("[SentinelRedisDataSource] subscribeFromChannel:::%s", channel);
        pubSubService.reg(channel, this);
    }

    @Override
    public void onMessage(String channel, String message) {
        log.debugf("[SentinelRedisDataSource] onMessage:::%s,%s", channel, message);
        getProperty().updateValue(parser.convert(message));
    }

    private void loadInitialConfig() {
        try {
            T newValue = loadConfig();
            if (newValue == null) {
                log.warn("[SentinelRedisDataSource] WARN: initial config is null, you may have to check your data source");
            }
            getProperty().updateValue(newValue);
        } catch (Exception ex) {
            log.warn("[SentinelRedisDataSource] Error when loading initial config", ex);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public String readSource() {
        return redisService.get(ruleKey);
    }
}
