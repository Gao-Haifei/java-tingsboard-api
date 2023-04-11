package TcpClient;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;


public class Tcp_Client {




    Timer timer;
    Socket socket;
    private String ip;
    private int port;
    private boolean Running = false;

    private TcpClientStateListener disconnectedListener;
    private TCPClientDataReceiveListener tcpClientDataReceiveListener;
    private TcpClientConnectListener connectListener;


    byte[] buffer = new byte[1024];


    public void setDisconnectedListener(TcpClientStateListener listener) {
        this.disconnectedListener = listener;
    }

    public void setDataReceiveListener(TCPClientDataReceiveListener listener) {
        this.tcpClientDataReceiveListener = listener;
    }

    public void setTcpClientConnectListener(TcpClientConnectListener listener) {
        this.connectListener = listener;
    }

    public Tcp_Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void Connect() {
        if (socket == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        socket = new Socket(ip, port);


                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Fun();

                            }
                        }, 0, 100);
                        Running = true;
                        OnConnect();
                    } catch (IOException e) {
                        e.printStackTrace();

                    }

                }
            }).start();
        }


    }

    private void OnDisConnect() {
        if (disconnectedListener != null) {
            disconnectedListener.onHandler();
        }
    }

    private void OnConnect() {
        if (connectListener != null) {
            connectListener.onConnect();
        }
    }




    private void Fun() {

        try {

            InputStream inputStream = socket.getInputStream();
            int size = inputStream.read(buffer, 0, buffer.length);
            if (size < 0) {
                close();
                return;
            }
            byte[] buff = new byte[size];

            System.arraycopy(buffer, 0, buff, 0, size);
            OnDataReceive(buff);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return Running;
    }

    private void OnDataReceive(byte[] buffer) {
        if (tcpClientDataReceiveListener != null) {
            tcpClientDataReceiveListener.onDataReceive(buffer);
        }
    }

    public interface TCPClientDataReceiveListener {
        void onDataReceive(byte[] var1);
    }

    public void close() {
        if (socket != null) {
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
                timer.cancel();
                Running = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            OnDisConnect();
        }
    }


    public interface TcpClientConnectListener {
        void onConnect();
    }

    public interface TcpClientStateListener {
        void onHandler();
    }

    public void Send(byte[] bytes) {
        try {
            if (socket != null) {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.write(bytes, 0, bytes.length);
                dos.flush();
            }
        } catch (Exception var3) {

        }
    }
}
