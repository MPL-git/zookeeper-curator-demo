package com.zookeeper.curator.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.data.Stat;

/**
 * @Description 注册监听器
 * @Auther pengl
 * @Version: 1.0
 * @Date 2021/4/26 10:26
 **/
@Slf4j
public class ZookeeperWatches {

    private CuratorFramework client;

    public ZookeeperWatches(CuratorFramework client) {
        this.client = client;
    }

    /**
     * Node Cache与Path Cache类似，Node Cache只是监听某一个特定的节点。它涉及到下面的三个类：
     * NodeCache - Node Cache实现类
     * NodeCacheListener - 节点监听器
     * ChildData - 节点数据
     **/
    public void znodeWatcher() throws Exception {
        NodeCache nodeCache = new NodeCache(client, "/node");
        nodeCache.start();
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                log.info("=======节点改变===========");

                String path = nodeCache.getPath();
                String currentDataPath = nodeCache.getCurrentData().getPath();
                String currentData = new String(nodeCache.getCurrentData().getData());
                Stat stat = nodeCache.getCurrentData().getStat();

                log.info("path:{}", path);
                log.info("currentDataPath:{}", currentDataPath);
                log.info("currentData:{}", currentData);
                log.info("stat:{}", stat);
            }
        });
        log.info("节点监听注册完成");
    }

    /**
     * Path Cache用来监控一个ZNode的子节点. 当一个子节点增加， 更新，删除时， Path Cache会改变它的状态， 会包含最新的子节点，
     * 子节点的数据和状态，而状态的更变将通过PathChildrenCacheListener通知。
     * 实际使用时会涉及到四个类：
     * PathChildrenCache
     * PathChildrenCacheEvent
     * PathChildrenCacheListener
     * ChildData
     **/
    public void znodeChildrenWatcher() throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, "/node",true);
        pathChildrenCache.start();
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                log.info("=======节点子节点改变===========");

                PathChildrenCacheEvent.Type type = event.getType();
                String childrenData = new String(event.getData().getData());
                String childrenPath = event.getData().getPath();
                Stat childrenStat = event.getData().getStat();

                log.info("子节点监听类型：{}", type);
                log.info("子节点路径：{}", childrenPath);
                log.info("子节点数据：{}", childrenData);
                log.info("子节点元数据：{}", childrenStat);
            }
        });
        log.info("子节点监听注册完成");
    }

    /**
     * Tree Cache可以监控整个树上的所有节点，类似于PathCache和NodeCache的组合，主要涉及到下面四个类：
     * TreeCache - Tree Cache实现类
     * TreeCacheListener - 监听器类
     * TreeCacheEvent - 触发的事件类
     * ChildData - 节点数据
     **/
    public void znodeTreeWatcher() throws Exception {
        TreeCache treeCache = new TreeCache(client, "/node");
        treeCache.start();
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                log.info("=======节点(tree)改变===========");

                TreeCacheEvent.Type type = treeCacheEvent.getType();
                String childrenData = new String(treeCacheEvent.getData().getData());
                String childrenPath = treeCacheEvent.getData().getPath();
                Stat childrenStat = treeCacheEvent.getData().getStat();

                log.info("节点(tree)监听类型：{}", type);
                log.info("节点(tree)路径：{}", childrenPath);
                log.info("节点(tree)数据：{}", childrenData);
                log.info("节点(tree)元数据：{}", childrenStat);
            }
        });
        log.info("节点(tree)监听注册完成");
    }


}
