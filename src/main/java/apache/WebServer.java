package apache;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import apache.utils.PropertiesUtils;


public class WebServer {

    private static final Properties props = PropertiesUtils.loadProperties();

    /**
     * 启动web服务器
     * 
     * @param port 服务器端口号
     */
    public void startup(int port) {

        Socket socket = null;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while(true){
            	System.out.println("开始启用线程处理请求.....");
                socket = serverSocket.accept();
                Processor processor = new Processor(socket, props);
                new Thread(processor).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try{
                if(socket != null) {
                    socket.close();
                }
            }catch(IOException e){
                e.printStackTrace();}
        }
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(props.getProperty("serverport"));
        if(args.length == 1){
            port = Integer.parseInt(args[0]);
        }
        new WebServer().startup(port);

    }

}
