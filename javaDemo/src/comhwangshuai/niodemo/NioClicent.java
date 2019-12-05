package comhwangshuai.niodemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * NioClicent
 */
public class NioClicent {
    public  void start() throws IOException {
        //链接服务器
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",8000));
        //向服务器发送数据
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNextLine()){
            String request = scanner.nextLine();
            if(request.length() > 0){
                socketChannel.write(Charset.forName("UTF-8").encode(request));
            }

        }
        //接收服务端响应
        /**
         * 新开线程，专门负责接收服务端的响应数据
         */
        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        new Thread(new NioClientHandler(selector)).start();
    }

    public static void main(String[] args) throws IOException{
         new NioClicent().start();
    }
}
