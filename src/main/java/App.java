import netty.http.server.HttpServer;
import netty.http.worker.controller.TestController;

/**
 * Created by shirukai on 2018/9/30
 * netty sever 启动类
 */
public class App {
    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.builder()
                .setPort(9090)
                .setController(TestController.class)
                .create().start();
    }
}
