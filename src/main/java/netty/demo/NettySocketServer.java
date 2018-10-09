package netty.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * Created by shirukai on 2018/9/30
 * 基于Netty实现的Socket Server
 */
public class NettySocketServer {
    public final Logger log = LoggerFactory.getLogger(this.getClass());
    private int port;

    public NettySocketServer(int port) {
        this.port = port;
    }

    public void start() {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        /*
         * 服务端可以使用handler和childHandler
         *  handler针对boss group起作用
         *  childrenHandler针对worker group起作用
         * 客户端只能使用handler
         */
        try {
            bootstrap
                    .option(ChannelOption.SO_BACKLOG, 1024) //todo search ？
                    .group(bossGroup, workerGroup) //绑定线程池
                    .channel(NioServerSocketChannel.class)// 指定使用的channel
                    .localAddress(port)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 绑定客户端时触发的操作
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            log.info("Client connected service：{}", socketChannel);
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new StringDecoder(Charset.forName("UTF-8")));
                            pipeline.addLast(new NettySocketServerHandler());//服务器处理客户端请求
                            pipeline.addLast(new StringEncoder(Charset.forName("UTF-8")));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind().sync(); //服务器异步创建绑定
            log.info("Server is listening：{}", channelFuture.channel());
            channelFuture.channel().closeFuture().sync();//关闭服务器
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            try {
                // 释放线程池资源
                workerGroup.shutdownGracefully().sync();
                bossGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }


    }

    public static void main(String[] args) {
        NettySocketServer server = new NettySocketServer(9099);
        server.start();
    }
}
