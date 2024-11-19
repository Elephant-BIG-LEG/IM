package com.elephant.user.server.impl;

import com.elephant.user.server.WebSocketServer;
import io.netty.channel.Channel;

/**
 * @Author: Elephant-FZY
 * @Email: 1085062843@qq.com
 * @ClassName: WebSocketServer
 * @Date: 2024/11/19/20:08
 * @Description: 处理请求的实体类
 */
public class WebSocketServerImpl implements WebSocketServer {

    /**
     * 用户登录
     * @param channel
     */
    @Override
    public void handleLoginReq(Channel channel) {

    }

    /**
     * 断开连接
     * @param channel
     */
    @Override
    public void removed(Channel channel) {

    }
}
