package netty.demo;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by shirukai on 2018/9/27
 * 非阻塞 I/O socket
 */
public class NioSocketServer {
    public static void main(String[] args) {
        try {
            // 1. 创建一个信道并设置信道为非阻塞
            ServerSocketChannel channel = ServerSocketChannel.open();
            // 设置信道为non-blocking（非阻塞） 默认为blocking
            channel.configureBlocking(false);
            // 2. 从信道中获取ServerSocket并绑定监听端口
            ServerSocket socket = channel.socket();
            // 获取本机的address
            InetSocketAddress address = new InetSocketAddress(9001);
            // 将端口绑定到ServerSocket上
            socket.bind(address);
            // 3. 创建一个Socket选择器
            Selector selector = Selector.open();
            // 4. 将选择器注册到各个信道上
            // SelectKey四种状态：OP_CONNECT 连接就绪、OP_ACCEPT 接收就绪、OP_READ 读就绪、OP_WRITE 写就绪
            channel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                // 5. 搜索信道
                selector.select();
                // 6. 获取准备好的信道所关联的key集合的iterator实例
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                // 7.遍历selectedKey,根据状态处理Socket
                while (keyIterator.hasNext()) {
                    // 获取key值
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();
                    try {
                        if (key.isAcceptable()) {
                            SocketChannel client = ((ServerSocketChannel) key.channel()).accept();
                            client.configureBlocking(false);
                            client.register(key.selector(), SelectionKey.OP_READ);
                            System.out.println("Accepted connection from: " + client);
                        }
                        if (key.isReadable()) {
                            SocketChannel client = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int readLength = client.read(buffer);
                            //如果有记录
                            if (readLength > 0) {
                                key.interestOps(SelectionKey.OP_READ);
                                String record = new String(buffer.array(), 0, readLength);
                                System.out.println(record);
                                client.write(ByteBuffer.wrap(("服务器已经接受消息：" + record).getBytes()));
                            }
                        }
                        if (key.isWritable()) {

                        }
                    } catch (Exception e) {
                        key.cancel();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
