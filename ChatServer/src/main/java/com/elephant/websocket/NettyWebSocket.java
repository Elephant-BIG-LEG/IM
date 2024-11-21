package com.elephant.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;


/**
 * @Author: Elephant-FZY
 * @Email: https://github.com/Elephant-BIG-LEG
 * @ClassName: NettyWebSocket
 * @Date: 2024/11/19/17:16
 * @Description: 建立服务端
 */
@Slf4j
public class NettyWebSocket {
    //端口
    private final int WEB_SOCKET_PORT = 6960;
    //自定义处理器
    public static final NettyWebSocketHandler NETTY_WEB_SOCKET_SERVER_HANDLER = new NettyWebSocketHandler();


    //创建线程执行器
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    /**
     * 启动
     * @throws InterruptedException 检查异常中断
     */
    public void start() throws InterruptedException{
        run();
    }

    /**
     * 销毁
     */
    public void destroy(){
        //先关闭EventLoopGroup
        Future<?> future = bossGroup.shutdownGracefully();
        Future<?> future1 = workerGroup.shutdownGracefully();
        //再关闭线程 syncUninterruptibly能够保证线程执行完任务后才关闭
        future.syncUninterruptibly();
        future1.syncUninterruptibly();
        log.info("Close the web server SUCCEED!");
    }

    /**
     * 执行
     * @throws InterruptedException 检查异常中断
     */
    public void run() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)//最大连接数量
                .option(ChannelOption.SO_KEEPALIVE, true)//一直连接
                .handler(new LoggingHandler(LogLevel.INFO)) // 为 bossGroup 添加 日志处理器
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        //TODO 看源码
                        //三十秒内客户端如果没有向服务器发送心跳则关闭
                        pipeline.addLast(new IdleStateHandler(30,0,0));
                        //使用Http解码器
                        pipeline.addLast(new HttpServerCodec());
                        //以 块 的方式进行处理
                        pipeline.addLast(new ChunkedWriteHandler());
                        /**
                         * 说明：
                         *  1. http数据在传输过程中是分段的，HttpObjectAggregator可以把多个段聚合起来；
                         *  2. 这就是为什么当浏览器发送大量数据时，就会发出多次 http请求的原因
                         */
                        pipeline.addLast(new HttpObjectAggregator(8192));
                        //保存用户ip
                        pipeline.addLast(new HttpHeadersHandler());
                        /**
                         * 说明：
                         *  1. 对于 WebSocket，它的数据是以帧frame 的形式传递的；
                         *  2. 可以看到 WebSocketFrame 下面有6个子类
                         *  3. 浏览器发送请求时： ws://localhost:7000/hello 表示请求的uri
                         *  4. WebSocketServerProtocolHandler 核心功能是把 http协议升级为 ws 协议，保持长连接；
                         *      是通过一个状态码 101 来切换的
                         */
                        pipeline.addLast(new WebSocketServerProtocolHandler("/"));
                        // 自定义handler ，处理业务逻辑
                        pipeline.addLast(NETTY_WEB_SOCKET_SERVER_HANDLER);
                    }
                });
        //启动服务端
        serverBootstrap.bind(WEB_SOCKET_PORT).sync();
    }
}
