package org.dtech.factoscan;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private DecoratedBarcodeView barcodeView;
    private Button btnext, btsave;
    private BeepManager beepManager;
    private String lastText;
    final List<String> arrayscan = new ArrayList<>();
    String sscan;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                //return;
            }

            lastText = result.getText();
            barcodeView.setStatusText(result.getText());
            barcodeView.pause();
            btnext.setVisibility(View.VISIBLE);
            btsave.setVisibility(View.VISIBLE);
            beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
            /*ImageView imageView = findViewById(R.id.barcodePreview);
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));*/
            arrayscan.add(result.getText());
            sscan= TextUtils.join("\n", arrayscan);
            TextView tprev = (TextView) findViewById(R.id.tPrev);
            tprev.setText(sscan);

        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barcodeView = findViewById(R.id.barcode_scanner);
        btnext = findViewById(R.id.btnext);
        btsave = findViewById(R.id.btsave);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.decodeContinuous(callback);

        beepManager = new BeepManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void next(View view) {
        barcodeView.resume();
    }

    public void resume(View view) {
        //barcodeView.resume();
        /*if (!fileExistance(Config.FIRST_TIME)) {
            launchWelcome();
        } else {
            new Loading().execute();
        }*/

        /*FileOutputStream outputStream;

        try {
            outputStream = openFileOutput("save-scan", Context.MODE_PRIVATE);
            outputStream.write(sscan.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        savePublic(MainActivity.this,  sscan);
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    public boolean fileExistance(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

    public void generateFile(Context context, String fileName, String body) {
        try {
            FileOutputStream fOut = openFileOutput(fileName+".txt",
                    MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            // Write the string to the file
            osw.write(body);
                        /* ensure that everything is
                         * really written out and close */
            osw.flush();
            osw.close();
            Toast.makeText(context, "Sukses", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*try {
            File root = new File(Environment.getExternalStorageDirectory(), "Facto-Scan");
            if (!root.exists()) {
                root.mkdirs();
            }
            File sFile = new File(*//*context.getFilesDir()*//*root, fileName+".txt");
            FileWriter writer = new FileWriter(sFile);
            writer.append(body);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Sukses", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void savePublic(Context context, String body) {
        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd-hh.mm.ss");
        String filename = format.format(now);
        try {
            File root = new File(Environment.getExternalStoragePublicDirectory(Environment.MEDIA_SHARED), "/facto");
            if (!root.exists()) {
                root.mkdirs();
            }
            File sFile = new File(/*context.getFilesDir()*/root, filename+".txt");
            FileWriter writer = new FileWriter(sFile);
            writer.append(body);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Sukses", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
