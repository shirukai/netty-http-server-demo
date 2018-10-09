package netty.demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

/**
 * Created by shirukai on 2018/9/30
 * netty socket client
 */
public class NettySocketClient {
    private String host;
    private int port;

    public NettySocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap
                    .group(group)//注册线程池
                    .channel(NioSocketChannel.class)// 使用NioSocketChannel来作为连接用的Channel类
                    .remoteAddress(host, port)//绑定远程服务的IP和端口
                    .handler(new ChannelInitializer<SocketChannel>() {//连接初始化
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // 输入流编码为StringEncoder
                            pipeline.addLast(new StringDecoder(Charset.forName("UTF-8")));
                            pipeline.addLast(new NettySocketClientHandler());
                            // 输出流编码为StringDecoder
                            pipeline.addLast(new StringEncoder(Charset.forName("UTF-8")));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        NettySocketClient client = new NettySocketClient("127.0.0.1", 9099);
        client.start();
    }
}
