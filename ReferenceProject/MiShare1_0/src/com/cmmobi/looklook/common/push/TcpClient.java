package com.cmmobi.looklook.common.push;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.apache.http.util.ByteArrayBuffer;

/**    
 * Tcp客户端程序，用于对服务端发送数据，并接收服务端的回应信息.
 * @author <a href="mailto:zhangwei@cmmobi.com">zhangwei</a>
 * @version 1.0 
 * Creation date: 2013-12-18
 */
public class TcpClient {
    private byte[] buffer = new byte[1024];

    private Socket ds = null;
    
    private String host = null;
    private int port = 0;

	private BufferedOutputStream sender;

	private BufferedInputStream receiver;

    /**
     * 构造函数，创建Tcp客户端
     * @throws Exception
     */
    public TcpClient(String host, int port) throws Exception {
    	this.host = host;
    	this.port = port;
    	
        ds = new Socket();
        ds.setReuseAddress(true);
        ds.bind(new InetSocketAddress(1984));
        ds.connect(new InetSocketAddress(host, port), 60*1000);
        //ds.setSoTimeout(0);
		sender = new BufferedOutputStream(ds.getOutputStream());
		receiver = new BufferedInputStream(ds.getInputStream());
    }
    
    
    public TcpClient(String tcpHost, int tcpPort, byte[] bytes) throws Exception {
		// TODO Auto-generated constructor stub
    	this(tcpHost, tcpPort);
    	send(bytes);
	}


	/**
     * 设置超时时间，该方法必须在bind方法之后使用.
     * @param timeout 超时时间
     * @throws Exception
     * @author <a href="mailto:zhangwei@cmmobi.com">zhangwei</a>
     */
    public final void setSoTimeout(final int timeout) throws Exception {
        ds.setSoTimeout(timeout);
    }

    /**
     * 获得超时时间.
     * @return 返回超时时间
     * @throws Exception
     * @author <a href="mailto:zhangwei@cmmobi.com">zhangwei</a>
     */
    public final int getSoTimeout() throws Exception {
        return ds.getSoTimeout();
    }

    public final Socket getSocket() {
        return ds;
    }

    /**
     * 向指定的服务端发送数据信息.
     * @param bytes 发送的数据信息
     * @throws IOException
     * @author <a href="mailto:zhangwei@cmmobi.com">zhangwei</a>
     */
    public final void send(final byte[] bytes) throws Exception {
        //OutputStream out = ds.getOutputStream();
    	if(sender==null){
    		throw new Exception("OutputStream is null");
    	}
    	
        if(bytes!=null){
        	sender.write(bytes);
        }
        sender.write(0);
        sender.flush();
    }

    /**
     * 接收从指定的服务端发回的数据.
     * @param lhost 服务端主机
     * @param lport 服务端端口
     * @return 返回从指定的服务端发回的数据.
     * @throws Exception
     * @author <a href="mailto:zhangwei@cmmobi.com">zhangwei</a>
     */
    public final String receive() throws Exception {

        //InputStream input = ds.getInputStream();
        if(receiver==null){
        	throw new Exception("InputStream is null");
        }
        
        
    	int len = 0;
        
        ArrayList<DataWrap> input_t = new ArrayList<DataWrap>();
        while((len=receiver.read(buffer, 0, buffer.length))>0){

        	if(buffer[len]==0){
        		if(len>0){
                	input_t.add(new DataWrap(buffer, len-1));
        		}

        		break; //EOF
        	}else{
            	input_t.add(new DataWrap(buffer, len));
        	}
        }
        
        int total_len = calcTotalLen(input_t);
        if(total_len>0 && input_t!=null && input_t.size()>0){
            byte[] array_t = new byte[total_len];
            int pos = 0;
            for(DataWrap dw : input_t){
            	System.arraycopy(dw.buf, 0, array_t, pos, dw.len);
            	pos += dw.len;
            }
            
            return new String(array_t);
        }else{
        	return null;
        }

    }

    private int calcTotalLen(ArrayList<DataWrap> input) {
		// TODO Auto-generated method stub
    	int total_len = 0;
    	if(input!=null && input.size()>0){
    		for(DataWrap data : input){
    			total_len += data.len;
    		}
    	}
		return total_len;
	}


	/**
     * 关闭Tcp连接.
     * @author <a href="mailto:zhangwei@cmmobi.com">zhangwei</a>
     */
    public final void close() {
        try {
        	if(ds!=null){
                ds.close();
                ds = null;
        	}

        	if(sender!=null){
        		sender.close(); 
        		sender = null;
        	}
			
        	if(receiver!=null){
    			receiver.close();
    			receiver = null;
        	}

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 测试客户端发包和接收回应信息的方法.
     * 联通地址 125.39.224.104
     * 电信地址 123.150.178.104  
     * 端口是18567
     * @param args
     * @throws Exception
     * @author <a href="mailto:zhangwei@cmmobi.com">zhangwei</a>
     */
    public static void main(String[] args) throws Exception {

        String serverHost = "123.150.178.104";
        int serverPort = 18567;
        TcpClient client = new TcpClient(serverHost, serverPort);
        client.send(("你好，阿蜜果!").getBytes());
        String info = client.receive();
        System.out.println("服务端回应数据：" + info);
    }
}