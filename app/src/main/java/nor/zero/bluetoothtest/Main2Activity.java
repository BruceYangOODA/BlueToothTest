package nor.zero.bluetoothtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main2Activity extends AppCompatActivity {

    private static final String SERVERIP = "192.168.0.101";
    private static final int SERVERPORT = 12345;
    private static Thread _thread = null;
    private Socket _sokcet = null;
    private BufferedReader _bufferedReader = null;
    private static PrintWriter _printerwriter = null;
    private String _meaasge = "";
    private final EditText editText = findViewById(R.id.editText01);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

       // final EditText editText = findViewById(R.id.editText01);
        Button connectBtn = findViewById(R.id.connectBtn);
        Button submitBtn = findViewById(R.id.submitBtn);
        connectBtn.setOnClickListener(connectBtnListener);
        submitBtn.setOnClickListener(submitBtnListener);

        _thread = new Thread(_runnable);
        _thread.start();



    }

    private Runnable _runnable = new Runnable() {
        @Override
        public void run() {
            while (true){
                try{
                    if((_meaasge=_bufferedReader.readLine())!= null){
                        _handler.sendMessage(_handler.obtainMessage());
                    }
                }
                catch (Exception e){e.printStackTrace();}
            }

        }
    };

    Handler _handler = new Handler(){
      public void handleMessage(Message msg){
          super.handleMessage(msg);

          try{
              System.out.println(_meaasge);
          }
          catch (Exception e){e.printStackTrace();}
      }
    };

    OnClickListener submitBtnListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String msg = editText.getText().toString();
            sendMessage(msg);
        }
    };

    public void sendMessage(String message){
        try{
            System.out.println(message);
            _printerwriter.print(message);
            _printerwriter.flush();
        }
        catch (Exception e){e.printStackTrace();}
    }

    OnClickListener connectBtnListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            try{
                _sokcet = new Socket(SERVERIP,SERVERPORT);
                _bufferedReader = new BufferedReader( new InputStreamReader(_sokcet.getInputStream(),"UTF-8"));
                _printerwriter = new PrintWriter(_sokcet.getOutputStream(),true);
            }
            catch (Exception e){e.printStackTrace();}
        }
    };


}
