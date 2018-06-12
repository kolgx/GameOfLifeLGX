package com.mindle.gameoflife;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

public class GamePageActivity extends AppCompatActivity {

    private static String TAG = "GamePageActivity";
    private static int sampleStatusIndex;

    private DrawingBoardSurfaceView surfaceView = null;
    private Button mPauseContinueBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);

        surfaceView = findViewById(R.id.surfaceView);
        RadioGroup speedGroup = findViewById(R.id.speedRadioGroup);
        Button iniBtn = findViewById(R.id.btn_start);
        mPauseContinueBtn = findViewById(R.id.btn_pause_continue);
        TextView aliveHintTextView = findViewById(R.id.aliveHintTextView);
        TextView generationTextView = findViewById(R.id.generationTextView);

        surfaceView.setSleepTime(1000);
        speedGroup.check(R.id.radioButton1);
        surfaceView.setAliveHintTextView(aliveHintTextView);
        surfaceView.setGenerationTextView(generationTextView);

        speedGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioButton1:
                    surfaceView.setSleepTime(1000);
                    break;
                case R.id.radioButton2:
                    surfaceView.setSleepTime(100);
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
            mPauseContinueBtn.setText("开始");
            mPauseContinueBtn.setTextColor(Color.GREEN);
            mPauseContinueBtn.setEnabled(true);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_load_sample:
                loadSample();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadSample() {
        sampleStatusIndex = sampleStatusIndex % StatusFactory.SAMPLE_STATUS_ARRAY.length;
        surfaceView.initGame(StatusFactory.SAMPLE_STATUS_ARRAY[sampleStatusIndex]);
        mPauseContinueBtn.setText("开始");
        mPauseContinueBtn.setTextColor(Color.GREEN);
        mPauseContinueBtn.setEnabled(true);
        setTitle(StatusFactory.SAMPLE_STATUS_ARRAY[sampleStatusIndex]);
        sampleStatusIndex++;
    }

    @Override
    protected void onStop() {
        super.onStop();
        onPauseButton();
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
