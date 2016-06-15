package apache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class Processor implements Runnable {

    private Socket socket;
    private InputStream in;
    private PrintStream out;
    private Properties props;

    public Processor() {
    }

    public Processor(Socket socket, Properties props) {
        this.socket = socket;
        this.props = props;
        try {
            in = this.socket.getInputStream();
            out = new PrintStream(this.socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String fileName = parseFile(in);
        if (StringUtils.isNotBlank(fileName)) {
            sendFile(fileName);
        }
    }

    private String parseFile(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String filename = null;
        try {
            String httMessage = br.readLine();
            System.out.println("httMessage====="+httMessage);
            if(StringUtils.isNotBlank(httMessage)) {
                String[] content = httMessage.split(" ");
                if(content.length!=3) {
                    sendErrorMessage(400, "客户端请求与语法错误!");
                    return null;
                }
                System.out.println("code: "+content[0]+" ,filename:"+content[1]+" ,http version:"+content[2]);
                filename = content[1];
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return filename;

    }

    private void sendFile(String filename) {
        File file = new File(props.getProperty("webroot") + filename);
        if(!file.exists()){
            sendErrorMessage(400, "请求资源不存在!");
        }
        try {
            InputStream in = new FileInputStream(file);
            byte content[] = new byte[(int)file.length()];
            in.read(content);
            out.println("HTTP/1.1 200 Query File");
            out.println("content-type: text/html; charset=utf-8");
            out.println("content-length:"+content.length);
            out.println();
            out.write(content);
            out.flush();
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendErrorMessage(int errorCode, String errorMessage) {
        out.println("HTTP/1.1 "+errorCode+" "+errorMessage);
        out.println("content-type: text/html; charset=utf-8");
        out.println();
        out.println("<html>");
        out.println("<title>错误消息");
        out.println("</title>");
        out.println("<body>");
        out.println("<h1>错误码:"+errorCode+",错误消息内容:"+errorMessage);
        out.println("</body>");
        out.println("</html>");
        out.flush();
        out.close();
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
