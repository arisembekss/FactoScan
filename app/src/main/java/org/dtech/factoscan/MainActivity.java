package org.dtech.factoscan;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
    Context context;
    RecyclerView recyclerView;
    AdapterQr mAdapter;
    String[] dum = {"tes", "dum"};
    List<String> arrayscan = new ArrayList<>();
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
            mAdapter.notifyItemInserted(arrayscan.size()-1);

            /*tprev = findViewById(R.id.tPrev);
            tprev.setText(sscan);*/

        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //arrayscan = new ArrayList(Arrays.asList(dum));
        mAdapter = new AdapterQr(MainActivity.this, arrayscan);
        barcodeView = findViewById(R.id.barcode_scanner);
        btnext = findViewById(R.id.btnext);
        btsave = findViewById(R.id.btsave);
        scrollView = findViewById(R.id.rprev);
        recyclerView = findViewById(R.id.recy);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39, BarcodeFormat.CODE_128
        , BarcodeFormat.CODABAR);
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

        sscan= TextUtils.join("\n", arrayscan);
        Log.d(TAG, "resume: "+sscan);
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
            //tprev.setText("");
            arrayscan.clear();

            mAdapter.notifyDataSetChanged();
            btnext.setVisibility(View.INVISIBLE);
            btsave.setVisibility(View.INVISIBLE);
            barcodeView.resume();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
