package com.wangshuai.niodemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * Nio server
 */
public class NioServer {
    public void start() throws IOException {
        //1.创建Selector
        Selector selector = Selector.open();
        //2.通过ServerSocketChannel创建channel通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //3.为channel通道绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress(8000));
        //4.设置channel为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //5.将channel注册到Selector上，监听事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器启动成功！");
        //6.循环等待新接入的链接
        for (; ; ) {
            //获取可用的Channel数量
            int readyChannels = selector.select();
            /*if(readyChannels == 0){
                continue;
            }*/
            if (readyChannels == 0) continue;
            //获取空channnel的集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = (SelectionKey) iterator.next();
                //移除当前的selectionKey
                iterator.remove();
                //7.根据就绪状态，调用应用方法处理业务逻辑
                //如果是接入事件
                if (selectionKey.isAcceptable()) {
                    acceptHandler(serverSocketChannel, selector);
                }
                //如果是可读事件
                if (selectionKey.isReadable()) {
                    readHandler(selectionKey, selector);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException{
         new NioServer().start();
    }

    //接入事件
    private void acceptHandler(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        //如果是接入事件，就创建socketChannel
        SocketChannel socketChannel = serverSocketChannel.accept();
        //socketChannel设置为非阻塞工作模式
        socketChannel.configureBlocking(false);
        //将channel注册到selector上，监听可读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        //回复客户端提示信息
        socketChannel.write(Charset.forName("UTF-8").encode("你与聊天室的其他人都不是朋友关系，请注意隐私安全！"));
    }

    //可读事件
    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        //要从selectionKey中获取到已经就绪的channel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        //创建buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //循环读取客户端的请求信息
        String request = "";
        while (socketChannel.read(byteBuffer) > 0) {
            byteBuffer.flip();
            request += Charset.forName("UTF-8").decode(byteBuffer);
        }
        //将Channel再次注册到selector上，监听他的可读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        //将客户端发送的请求信息广播给其他客户端
        if (request.length() > 0) {
            System.out.println(":: " + request);
            boardcast(selector, socketChannel, request);
        }
    }

    //广播给其他客户端
    private void boardcast(Selector selector, SocketChannel souceChannel, String request
    ) {
        //获取所有接入的客户端
        Set<SelectionKey> selectionKeys = selector.keys();
        selectionKeys.forEach(s -> {
            Channel channel = s.channel();
            //剔除发消息的客户端
            if (channel instanceof SocketChannel && channel != souceChannel) {
                try {
                    ((SocketChannel) channel).write(Charset.forName("UTF-8").encode(request));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //循环向所有Channel广播信息
    }
}
