package com.elephant.websocket;

import cn.hutool.json.JSONUtil;
import com.elephant.common.domin.enums.WSReqTypeEnum;
import com.elephant.common.domin.enums.WSBaseResp;
import com.elephant.common.server.WebSocketServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.extra.spring.SpringUtil;


/**
 * @Author: Elephant-FZY
 * @Email: https://github.com/Elephant-BIG-LEG
 * @ClassName: NettyWebSocketHandler
 * @Date: 2024/11/19/17:16
 * @Description: 服务端处理器
 */
@Slf4j
public class NettyWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private WebSocketServer webSocketServer;

    /**
     * 处理用户请求报文
     * @param ctx 上下文
     * @param msg 用户请求报文
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        WSBaseResp wsBaseResp = JSONUtil.toBean(msg.text(), WSBaseResp.class);
        WSReqTypeEnum wsReqTypeEnum = WSReqTypeEnum.of(wsBaseResp.getType());
        switch (wsReqTypeEnum) {
            case LOGIN:
                this.webSocketServer.handleLoginReq(ctx.channel());
                log.info("Request a QR code = " + msg.text());
                break;
            case HEARTBEAT:
                break;
            default:
                log.info("Unknown type");
        }
    }


    /**
     * 用户连接WebSocket服务时，调用该方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.webSocketServer = getService();
    }

    /**
     * 断开连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        userOffLine(ctx);
    }


    /**
     * 取消绑定
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error("Triggering channelInactive causes the connection to drop，the channel id:{}",ctx.channel().id());
        userOffLine(ctx);
    }

    /**
     * 心跳检测
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete){
            log.info("Connection is SUCCEED!");
        }else if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state().equals(IdleStateEvent.READER_IDLE_STATE_EVENT)){
                log.info("Read Leisure");
                //断开连接
                ctx.channel().close();
            }
        }
    }

    /**
     * 获取服务
     * @return
     */
    private WebSocketServer getService() {
        return SpringUtil.getBean(WebSocketServer.class);
    }

    /**
     * 离线功能
     * @param ctx
     */
    private void userOffLine(ChannelHandlerContext ctx) {
        this.webSocketServer.removed(ctx.channel());
        ctx.channel().close();
    }
    /**
     * 异常捕获
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Exception Occurrenced,the info:{}",cause);
        ctx.channel().close();
    }
}
