package com.sdu.activemq.core.broker.client;

import com.sdu.activemq.core.MQConfig;
import io.netty.channel.ChannelInboundHandler;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 每个MQ Server Broker有个连接池
 *
 * @author hanhan.zhang
 * */
public class BrokerTransportPool extends GenericObjectPool<BrokerTransport> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerTransportPool.class);

    public static final String BROKER_CONNECT_MAX_ACTIVE = "broker.connect.max.active";

    public static final String BROKER_CONNECT_MIN_IDLE = "broker.connect.min.idle";

    public static final String BROKER_CONNECT_MAX_IDLE = "broker.connect.max.idle";

    public static final String BROKER_CONNECT_MAX_AWAIT = "broker.connect.max.await";

    public static final String BROKER_CONNECT_SESSION_TIMEOUT = "broker.connect.session.timeout";

    private String brokerAddress;

    public BrokerTransportPool(String brokerAddress, MQConfig mqConfig, ChannelInboundHandler channelHandler) {
        this.brokerAddress = brokerAddress;
        int maxActive = mqConfig.getInt(BROKER_CONNECT_MAX_ACTIVE, 10);
        int minIdle = mqConfig.getInt(BROKER_CONNECT_MIN_IDLE, 5);
        int maxIdle = mqConfig.getInt(BROKER_CONNECT_MAX_IDLE, 20);
        int maxWait = mqConfig.getInt(BROKER_CONNECT_MAX_AWAIT, 1000);
        int sessionTimeOut = mqConfig.getInt(BROKER_CONNECT_SESSION_TIMEOUT, 5000);

        LOGGER.info("BrokerTransportPool[maxActive=%d,minIdle=%d,maxIdle=%d,maxWait=%d,sessionTimeOut=%d]", maxActive, minIdle, maxIdle, maxWait, sessionTimeOut);

        this.setMaxActive(maxActive);
        this.setMaxIdle(maxIdle);
        this.setMinIdle(minIdle);
        this.setMaxWait(maxWait);
        this.setTestOnBorrow(false);
        this.setTestOnReturn(false);
        this.setTimeBetweenEvictionRunsMillis(10 * 1000);
        this.setNumTestsPerEvictionRun(maxActive + maxIdle);
        this.setMinEvictableIdleTimeMillis(30 * 60 * 1000);
        this.setTestWhileIdle(true);

        this.setFactory(new TransportPoolFactory(brokerAddress, mqConfig, channelHandler));
    }

    public String getBrokerAddress() {
        return brokerAddress;
    }

    public void destroy() throws Exception {
        this.close();
    }
}