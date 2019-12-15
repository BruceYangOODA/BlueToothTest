package nor.zero.bluetoothtest;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;
import android.widget.ViewAnimator;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import nor.zero.common.activities.SampleActivityBase;
import nor.zero.common.logger.Log;
import nor.zero.common.logger.LogFragment;
import nor.zero.common.logger.LogWrapper;
import nor.zero.common.logger.MessageOnlyLogFilter;


public class MainActivity extends SampleActivityBase {

    public static final String TAG = "MainActivity";

    // Whether the Log Fragment is currently shown
    private boolean mLogShown;
    private TextView tvConnect;
    public ImageView imgEchi;

    private final String IMAGE_TYPE = "image/*";
    private static final int IMG_FILE_SELECT_REQUEST_CODE = 49;
    private static Uri imgUri;
    private static Bitmap bitmap = null;

    private static boolean isReceive = false;
    private Button btnReceive;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

   //     tvConnect = findViewById(R.id.tvConnect);
    //    tvConnect.setOnClickListener(connectListener);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BluetoothChatFragment fragment = new BluetoothChatFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
        Button btnLoadImg = findViewById(R.id.btnLoadImg);
        Button btnTransfer = findViewById(R.id.btnTransfer);
        btnReceive = findViewById(R.id.btnReceive);
        imgEchi = findViewById(R.id.imgEcchi);
        btnLoadImg.setOnClickListener(btnLoadImgClickListener);
        btnTransfer.setOnClickListener(btnTranferClickListener);
        btnReceive.setOnClickListener(btnReceiveClickListener);

        btnReceive.setText("接收照片");


    }

    OnClickListener btnReceiveClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            BluetoothChatFragment fragment = (BluetoothChatFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.sample_content_fragment);
            ByteArrayOutputStream imgData = fragment.mChatService.baos;

            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inPreferQualityOverSpeed = true;
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inSampleSize = 1;
            options.inScaled = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Bitmap bitmap = BitmapFactory.decodeByteArray(imgData.toByteArray(), 0, imgData.toByteArray().length,options);
   //         Bitmap bitmap = Bitmap.createBitmap(2400,1600,Bitmap.Config.ARGB_8888);
            Toast.makeText(MainActivity.this,"接收照片",Toast.LENGTH_SHORT).show();
        //    ByteBuffer byteBuffer = ByteBuffer.wrap(imgData.toByteArray());
          //  byteBuffer.position(0);
       //     bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(imgData.toByteArray()));
        //    bitmap.copyPixelsFromBuffer(byteBuffer);
            imgEchi.setImageBitmap(bitmap);
            Log.v("aaa","檔案長度: "+bitmap.getByteCount());
            fragment.mChatService.baos = new ByteArrayOutputStream();

            /*
            isReceive =! isReceive;
            if(isReceive){
                BluetoothChatFragment fragment = (BluetoothChatFragment)getSupportFragmentManager()
                        .findFragmentById(R.id.sample_content_fragment);
                btnReceive.setText("接收照片");
                fragment.mChatService.mState = BluetoothChatService.STATE_TRANSFER;
            }
            else {
                BluetoothChatFragment fragment = (BluetoothChatFragment)getSupportFragmentManager()
                        .findFragmentById(R.id.sample_content_fragment);
                btnReceive.setText("接收信息");
                fragment.mChatService.mState = BluetoothChatService.STATE_CONNECTED;
            }*/
        }
    };


    OnClickListener btnTranferClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

/*            try{
                InputStream imgData = getContentResolver().openInputStream(imgUri);

            }catch (Exception e){}*/
            BluetoothChatFragment fragment = (BluetoothChatFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.sample_content_fragment);
             Bitmap bitmap = ((BitmapDrawable)imgEchi.getDrawable()).getBitmap();
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
             byte[] imageInByte = baos.toByteArray();

            if (fragment.mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
                Toast.makeText(MainActivity.this, R.string.not_connected, Toast.LENGTH_SHORT).show();
                return;
            }
             fragment.mChatService.transfer(imageInByte);

        }
    };

    OnClickListener btnLoadImgClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType(IMAGE_TYPE);
            startActivityForResult(intent,IMG_FILE_SELECT_REQUEST_CODE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case IMG_FILE_SELECT_REQUEST_CODE:
                if(resultCode == RESULT_OK)
                {
                    try{
                        imgUri = (Uri)data.getData();
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
                        imgEchi.setImageBitmap(bitmap);
                        android.util.Log.v("aaa","imgUri ; "+imgUri);
                    }
                    catch (Exception e){e.printStackTrace();}
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
        logToggle.setTitle(mLogShown ? R.string.sample_hide_log : R.string.sample_show_log);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.menu_toggle_log:
                mLogShown = !mLogShown;
                ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
                if (mLogShown) {
                    output.setDisplayedChild(1);
                } else {
                    output.setDisplayedChild(0);
                }
                supportInvalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Create a chain of targets that will receive log data */
    @Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());

        Log.i(TAG, "Ready");
    }
}
