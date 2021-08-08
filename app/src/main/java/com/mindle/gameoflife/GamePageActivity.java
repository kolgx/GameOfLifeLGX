package com.mindle.gameoflife;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GamePageActivity extends AppCompatActivity {

    private static final int READ_PERMISSION_REQUEST = 100;
    private static final int OPEN_DOCUMENT_REQUEST = 101;

    private static String TAG = "GamePageActivity";
    private static String LIMIT_ON = "开启边界线";
    private static String LIMIT_OFF = "关闭边境线";
    private static int sampleStatusIndex;

    private DrawingBoardSurfaceView surfaceView = null;
    private Button mPauseContinueBtn;
    private ProgressBar mProgressBar;

    private String jsonFileName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);

        surfaceView = findViewById(R.id.surfaceView);
        RadioGroup speedGroup = findViewById(R.id.speedRadioGroup);
        Button iniBtn = findViewById(R.id.btn_start);
        mPauseContinueBtn = findViewById(R.id.btn_pause_continue);
        mProgressBar = findViewById(R.id.model_progress_bar);
        TextView aliveHintTextView = findViewById(R.id.aliveHintTextView);
        TextView generationTextView = findViewById(R.id.generationTextView);

        surfaceView.setSleepTime(1000);
        speedGroup.check(R.id.radioButton1);
        surfaceView.setAliveHintTextView(aliveHintTextView);
        surfaceView.setGenerationTextView(generationTextView);
        surfaceView.setmButton(mPauseContinueBtn);
        mProgressBar.setVisibility(View.GONE);

        speedGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioButton1:
                    surfaceView.setSleepTime(1000);
                    break;
                case R.id.radioButton2:
                    surfaceView.setSleepTime(500);
                    break;
                case R.id.radioButton3:
                    surfaceView.setSleepTime(100);
                    break;
                case R.id.radioButton4:
                    surfaceView.setSleepTime(50);
                    break;
            }
        });
        iniBtn.setOnClickListener((v -> {
            setTitle(R.string.app_name);
            surfaceView.initGame();
            afterInitGame();
        }));
        mPauseContinueBtn.setOnClickListener(v -> {
            if ("暂停".contentEquals(mPauseContinueBtn.getText())) {
                onPauseButton();
            } else {
                onContinueButton();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        onPauseButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OPEN_DOCUMENT_REQUEST && resultCode == RESULT_OK
                && data.getData() != null) {
            Uri uri = data.getData();
            grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            beginLoadJson(uri);
        }
    }

    MenuItem menuss = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuss = menu.findItem(R.id.menu_limit);
        if(surfaceView.isLimit())menuss.setTitle(LIMIT_OFF);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_load_json:
                checkReadPermissionThenOpen();
                return true;
            case R.id.menu_load_sample:
                loadSampleArray();
                return true;
            case R.id.menu_about:
                showAboutDialog();
                return true;
            case R.id.menu_limit:
                if(LIMIT_ON.equals(menuss.getTitle())){
                    menuss.setTitle(LIMIT_OFF);
                    surfaceView.setLimit(true);
                }
                else {
                    menuss.setTitle(LIMIT_ON);
                    surfaceView.setLimit(false);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    beginOpenJson();
                } else {
                    Toast.makeText(this, R.string.read_permission_failed,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(R.string.about_text)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void beginLoadJson(Uri uri) {
        mProgressBar.setVisibility(View.VISIBLE);
        new JsonFileLoadTask().execute(uri);
    }

    private class JsonFileLoadTask extends AsyncTask<Uri, Integer, String> {

        @Override
        protected String doInBackground(Uri... uris) {
            Uri uri = uris[0];
            InputStream stream = null;
            try {
                ContentResolver cr = getApplicationContext().getContentResolver();
                String fileName = MyUtils.getFileName(cr, uri);
                if (!TextUtils.isEmpty(fileName)) {
                    jsonFileName = fileName;
                }

                if ("http".equals(uri.getScheme()) || "https".equals(uri.getScheme())) {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(uri.toString()).build();
                    Response response = client.newCall(request).execute();

                    // TODO: figure out how to NOT need to read the whole file at once.
                    stream = new ByteArrayInputStream(response.body().bytes());
                } else {
                    stream = cr.openInputStream(uri);
                }

                if (stream != null) {
                    return MyUtils.readString(stream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                MyUtils.closeSilently(stream);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (isDestroyed()) {
                return;
            }
            if (s != null) {
                setInitArray(s);
            } else {
                Toast.makeText(getApplicationContext(), R.string.open_model_error,
                        Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

    private void setInitArray(String s) {
        try {
            int[][] matrix = MyUtils.parseJsonArray(s);
            if (matrix != null) {
                setTitle(jsonFileName);
                surfaceView.initGame(matrix);
                afterInitGame();
            } else {
                Toast.makeText(getApplicationContext(), R.string.parse_json_error,
                        Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), R.string.parse_json_error,
                    Toast.LENGTH_SHORT).show();
        } finally {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void afterInitGame() {
        mPauseContinueBtn.setText("开始");
        mPauseContinueBtn.setTextColor(Color.GREEN);
        mPauseContinueBtn.setEnabled(true);
    }

    private void checkReadPermissionThenOpen() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_PERMISSION_REQUEST);
        } else {
            beginOpenJson();
        }
    }

    private void beginOpenJson() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        startActivityForResult(intent, OPEN_DOCUMENT_REQUEST);
    }

    private void loadSampleArray() {
        sampleStatusIndex = sampleStatusIndex % StatusFactory.SAMPLE_STATUS_ARRAY.length;
        surfaceView.initGame(StatusFactory.SAMPLE_STATUS_ARRAY[sampleStatusIndex]);
        afterInitGame();
        setTitle(StatusFactory.SAMPLE_STATUS_ARRAY[sampleStatusIndex]);
        sampleStatusIndex++;
    }

    private void onPauseButton() {
        surfaceView.pauseGame();
        mPauseContinueBtn.setText("开始");
        mPauseContinueBtn.setTextColor(Color.GREEN);
    }

    private void onContinueButton() {
        surfaceView.continueGame();
        mPauseContinueBtn.setText("暂停");
        mPauseContinueBtn.setTextColor(Color.RED);
    }

}
