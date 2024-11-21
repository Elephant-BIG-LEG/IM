package com.elephant.common.server;

import io.netty.channel.Channel;
import me.chanjar.weixin.common.error.WxErrorException;

/**
 * @Author: Elephant-FZY
 * @Email: https://github.com/Elephant-BIG-LEG
 * @ClassName: WebSocketServer
 * @Date: 2024/11/19/20:06
 * @Description: 处理客户端的所有请求
 */
public interface WebSocketServer {
    /**
     * 处理用户登录 返回一张二维码
     * @param channel
     */
    void handleLoginReq(Channel channel) throws WxErrorException;
    /**
     * 断开连接
     * @param channel
     */
    void removed(Channel channel);
}
