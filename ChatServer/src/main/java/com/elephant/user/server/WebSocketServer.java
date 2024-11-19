package com.elephant.user.server;

import io.netty.channel.Channel;

/**
 * @Author: Elephant-FZY
 * @Email: 1085062843@qq.com
 * @ClassName: WebSocketServer
 * @Date: 2024/11/19/20:06
 * @Description: 处理客户端的所有请求
 */
public interface WebSocketServer {
    /**
     * 处理用户登录 返回一张二维码
     * @param channel
     */
    void handleLoginReq(Channel channel);
    /**
     * 断开连接
     * @param channel
     */
    void removed(Channel channel);
}
