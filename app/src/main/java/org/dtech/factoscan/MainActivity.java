package org.dtech.factoscan;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import java.io.FileWriter;
import java.io.IOException;
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
    private ScrollView scrollView;
    private TextView tprev;
    private BeepManager beepManager;
    private String lastText;
    final List<String> arrayscan = new ArrayList<>();
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 375);
    RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 180);
    FloatingActionButton fab;
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
            tprev = findViewById(R.id.tPrev);
            tprev.setText(sscan);

        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        barcodeView = findViewById(R.id.barcode_scanner);
        btnext = findViewById(R.id.btnext);
        btsave = findViewById(R.id.btsave);
        scrollView = findViewById(R.id.rprev);

        fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                /*barcodeView.setVisibility(View.INVISIBLE);
                scrollView.setLayoutParams(params);
                btnext.setText("Resume");
                fab.setVisibility(View.INVISIBLE);*/
            }
        });
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
        if (btnext.getText().equals("Next")) {
            barcodeView.resume();
        } else {
            /*barcodeView.setVisibility(View.VISIBLE);
            scrollView.setLayoutParams(params2);

            btnext.setText("Next");
            fab.setVisibility(View.VISIBLE);*/
        }

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
            tprev.setText("");
            arrayscan.clear();
            btnext.setVisibility(View.INVISIBLE);
            btsave.setVisibility(View.INVISIBLE);
            barcodeView.resume();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
