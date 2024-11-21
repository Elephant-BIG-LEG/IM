package com.elephant.common.server.impl;


import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.elephant.common.domin.dto.WSChannelExtraDTO;
import com.elephant.common.domin.enums.WSBaseResp;
import com.elephant.common.server.WebSocketServer;
import com.elephant.common.server.adapter.WebSocketAdapter;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Elephant-FZY
 * @Email: https://github.com/Elephant-BIG-LEG
 * @ClassName: WebSocketServer
 * @Date: 2024/11/19/20:08
 * @Description: 处理请求的实体类
 */
@Component
@Slf4j
public class WebSocketServerImpl implements WebSocketServer {

    //TODO 要配置自己的微信号
    @Autowired
    private WxMpService wxMpService;

    /**
     * 保存在线用户 - 通道映射
     * key: Channel
     * value: Uid
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    public static final long MAXIMUM_SIZE = 10000L;
    public static final Duration EXPIRE_TIME = Duration.ofHours(1);
    /**
     * 临时保存用户登录码
     * key: 随机登录码
     * value: Channel
     */
    private static final Cache<Integer,Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .expireAfterWrite(EXPIRE_TIME)
            .maximumSize(MAXIMUM_SIZE)
            .build();

    /**
     * 用户登录 --- 使用微信登录方案
     * @param channel
     */
    @Override
    public void handleLoginReq(Channel channel) throws WxErrorException {
        //获取随机登录码
        Integer code = generateLoginCode(channel);
        //请求微信接口
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code,(int)EXPIRE_TIME.getSeconds());
        sendMsg(channel, WebSocketAdapter.buildResp(wxMpQrCodeTicket));
    }

    /**
     * 发送消息
     * @param channel
     * @param resp
     */
    private void sendMsg(Channel channel, WSBaseResp<?> resp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(resp)));
    }


    /**
     * 获取不重复的登录的code，微信要求最大不超过int的存储极限
     * 防止并发，可以给方法加上synchronize，也可以使用cas乐观锁
     *
     * @return
     */
    private Integer generateLoginCode(Channel channel) {
        int inc;
        do {
//            //本地cache时间必须比redis key过期时间短，否则会出现并发问题
//            inc = RedisUtils.integerInc(RedisKey.getKey(LOGIN_CODE), (int) EXPIRE_TIME.toMinutes(), TimeUnit.MINUTES);
            inc = RandomUtil.randomInt(Integer.MAX_VALUE);
        } while (WAIT_LOGIN_MAP.asMap().containsKey(inc));
        //储存一份在本地
        WAIT_LOGIN_MAP.put(inc, channel);
        return inc;
    }

    /**
     * 断开连接
     * @param channel
     */
    @Override
    public void removed(Channel channel) {

    }
}
