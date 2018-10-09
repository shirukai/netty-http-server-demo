package netty.demo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by shirukai on 2018/9/27
 * 阻塞I/O socket
 */
public class OioSocketServer {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(9000);
            while (true) {
                Socket socket = server.accept();
                new Thread(() -> {
                    try {
                        //读取客户端发来的数据
                        InputStream is = socket.getInputStream();//字节输入流
                        InputStreamReader isr = new InputStreamReader(is);//字节流转为字符流
                        BufferedReader br = new BufferedReader(isr);//为字符流添加缓冲
                        String info = null;
                        while ((info = br.readLine()) != null) {
                            System.out.println("客户端发来信息：" + info);
                        }
                        //返回客户端信息
                        OutputStream os = socket.getOutputStream();
                        os.write("已经收到信息".getBytes(Charset.forName("UTF-8")));
                        os.flush();
                        br.close();
                        isr.close();
                        is.close();
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
