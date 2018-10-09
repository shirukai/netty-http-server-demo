package netty.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import netty.http.server.entity.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by shirukai on 2018/9/30
 * 基于netty 实现httpSever
 */
public class HttpServer {
    private HttpServerConfig config;
    private int port;
    private Map<String, Router> routers;

    public final Logger log = LoggerFactory.getLogger(this.getClass());

    public HttpServerConfig builder() {
        this.config = new HttpServerConfig(this);
        return config;
    }

    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            bootstrap
                    .group(group)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline()
                                    .addLast("decoder", new HttpRequestDecoder())
                                    .addLast("encoder", new HttpResponseEncoder())
                                    .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
                                    .addLast("handler", new HttpServerHandler(routers));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);
            bootstrap.bind(port).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Start app server at port:{}", port);
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setRouters(Map<String, Router> routers) {
        this.routers = routers;
    }
}
