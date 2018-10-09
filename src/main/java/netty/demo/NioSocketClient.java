package netty.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Created by shirukai on 2018/9/27
 * Nio Socket Client
 */
public class NioSocketClient {
    public static void main(String[] args) {
        try {
            // 1. 创建一个信道,并绑定远程服务器的ip和端口
            SocketChannel channel = SocketChannel.open();
            // 设置服务器的address
            InetSocketAddress remote = new InetSocketAddress("127.0.0.1", 9001);
            // 2. 连接远程服务器
            channel.connect(remote);
            channel.configureBlocking(false);
            // 3. 创建一个Socket选择器
            Selector selector = Selector.open();
            // 4. 将选择器注册到信道中
            channel.register(selector, SelectionKey.OP_READ);
            // 5. 启动一个线程用来接收服务器消息
            new Thread(() -> {
                while (true) {
                    //读取数据
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int length = 0;
                    try {
                        length = channel.read(buffer);
                        if (length > 0) {
                            System.out.println(new String(buffer.array(), 0, length));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            // 6. 从键盘获取输入发送给服务器
            Scanner scanner = new Scanner(System.in);
            System.out.println("输入数据:\n");
            while (scanner.hasNextLine()) {
                System.out.println("输入数据:\n");
                // 读取键盘的输入
                String line = scanner.nextLine();
                // 将键盘的内容输出到SocketChannel中
                channel.write(Charset.forName("UTF-8").encode(line));
            }
            scanner.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

