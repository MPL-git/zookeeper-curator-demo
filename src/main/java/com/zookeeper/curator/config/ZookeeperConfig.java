package com.zookeeper.curator.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description zookeeper框架curator配置
 * @Auther pengl
 * @Version: 1.0
 * @Date 2021/4/26 10:17
 **/
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "zookeeper.curator")
public class ZookeeperConfig {

    /**
     * 集群地址
     */
    private String ip;

    /**
     * 连接超时时间
     */
    private Integer connectionTimeoutMs;
    /**
     * 会话超时时间
     */
    private Integer sessionTimeOut;

    /**
     * 重试机制时间参数
     */
    private Integer sleepMsBetweenRetry;

    /**
     * 重试机制重试次数
     */
    private Integer maxRetries;

    /**
     * 命名空间(父节点名称)
     */
    private String namespace;

    /**
     * session 重连策略
     *
     * RetryPolicy retry Policy = new RetryOneTime(3000);
     * 说明：三秒后重连一次，只重连一次
     *
     * RetryPolicy retryPolicy = new RetryNTimes(3, 3000);
     * 说明：每三秒重连一次，重连三次
     *
     * RetryPolicy retryPolicy = new RetryUntilElapsed(1000, 3000);
     * 说明：每三秒重连一次，总等待时间超过个 10 秒后停止重连
     *
     * RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
     * 说明：这个策略的重试间隔会越来越长
     * 公式： baseSleepTImeMs * Math.max(1,random.nextInt(1 << (retryCount + 1)))
     * baseSleepTimeMs = 1000 例子中的值
     * maxRetries = 3 例子中的值
     **/
    @Bean("curatorClient")
    public CuratorFramework curatorClient() throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                //连接地址  集群用,隔开
                .connectString(ip)
                .connectionTimeoutMs(connectionTimeoutMs)
                //会话超时时间
                .sessionTimeoutMs(sessionTimeOut)
                //设置重试机制
                .retryPolicy(new ExponentialBackoffRetry(sleepMsBetweenRetry, maxRetries))
                //设置命名空间 在操作节点的时候，会以这个为父节点
                .namespace(namespace)
                .build();
        client.start();

        //注册监听器
        ZookeeperWatches watches = new ZookeeperWatches(client);
        watches.znodeWatcher();
        watches.znodeChildrenWatcher();
        watches.znodeTreeWatcher();
        return client;
    }


}
