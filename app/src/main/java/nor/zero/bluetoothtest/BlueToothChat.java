package nor.zero.bluetoothtest;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class BlueToothChat extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter ;
    private static int REQUEST_ENABLE_BT = 123;
    private static int STATE_NONE = 0;
    private BluetoothChatService bluetoothChatService;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth_chat);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothChatService = new BluetoothChatService(this,handler);


    }


    @Override
    protected void onStart() {
        super.onStart();

        if(!bluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }
        else {
        //    if(bluetoothChatService == null)
              //  setupChat();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

      //  if(bluetoothChatService.getState() == BluetoothChatService.STATE_NONE){
       //     bluetoothChatService.start();
       // }
    }
}
