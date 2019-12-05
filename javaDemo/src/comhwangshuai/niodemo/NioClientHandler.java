package comhwangshuai.niodemo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class NioClientHandler implements Runnable {
    private Selector selector;

    public NioClientHandler(Selector selector) {
        this.selector = selector;
    }

    public void run() {
        try {
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
                    //如果是可读事件
                    if (selectionKey.isReadable()) {
                        readHandler(selectionKey, selector);
                    }
                }
            }
        } catch (Exception e) {

        }

    }

    //可读事件
    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        //要从selectionKey中获取到已经就绪的channel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        //创建buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //循环读取服务端的响应信息
        String request = "";
        while (socketChannel.read(byteBuffer) > 0) {
            byteBuffer.flip();
            request += Charset.forName("UTF-8").decode(byteBuffer);
        }
        //将Channel再次注册到selector上，监听他的可读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        //将服务端的响应信息打印到本地
        if (request.length() > 0) {
            System.out.println(":: " + request);
        }
    }

    ;

}
