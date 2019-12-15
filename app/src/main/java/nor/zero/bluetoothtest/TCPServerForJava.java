package nor.zero.bluetoothtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class TCPServerForJava {

    private static final int SERVERPORT=12345;
    private static List<Socket> _clientList = new ArrayList<Socket>();
    private static ExecutorService _executorService ;
    private static ServerSocket _serverSocket;

    public static void main(String[] args){
        try{
            _serverSocket = new ServerSocket(SERVERPORT);

            //建立一個執行緒池
      //      _executorService = Executor.newCachedThreadPool();
            System.out.println("等待Android用戶端程式的連接...");
            Socket client = null;

            while (true){
                client = _serverSocket.accept();
                _clientList.add(client);
      //          _executorService.execute(new ThredServer(client));
            }
        }
        catch (Exception e){e.printStackTrace();}
    }

    static class ThreadServer implements Runnable{

        private Socket _socket;
        private BufferedReader _bufferedReader;
        private PrintWriter _printWriter;
        private String _message;

        public ThreadServer(Socket socket) throws IOException{
            this._socket = socket;
            _bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //輸出連接資訊
            _message = "connect with " + this._socket.getInetAddress() + " port " + this._socket.getPort();

            sendMessage(_message);
        }

        public void run(){
            try{
                while ((_message = _bufferedReader.readLine()) != null){
                    if(_message.trim().equals("exit")){
                        //當一個用戶端退出時
                        _clientList.remove(_socket);
                        _bufferedReader.close();
                        _printWriter.close();
                        _message = " connect with " +this._socket.getInetAddress() + " port " + this._socket.getPort();
                        _socket.close();
                        sendMessage(_message);

                        break;
                    }
                    else {
                        _message = _socket.getInetAddress() + ":" + _message;
                        sendMessage(_message);
                    }
                }
            }
            catch (Exception e){e.printStackTrace();}
        }

        private void sendMessage(String message) throws IOException{
            System.out.println(message);
            for(Socket client : _clientList){
                _printWriter = new PrintWriter(client.getOutputStream(),true);
                _printWriter.println(_message);
            }
        }
    }




}

