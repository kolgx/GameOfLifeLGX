package com.mindle.gameoflife;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class DrawingBoardSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "DrawingBoardSurfaceView";
    private static final int MESSAGE_UPDATA_UI = 0;
    private static int num = 100;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATA_UI: {
                    int[] data = (int[]) msg.obj;
                    aliveHintTextView.setText(String.valueOf(data[0]));
                    generationTextView.setText(String.valueOf(data[1]));
                    break;
                }
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    private float mWidth;
    private Creatures creatures;
    private TextView aliveHintTextView;
    private TextView generationTextView;
    private float mGridUnit;

    private AtomicLong sleepTime = new AtomicLong(1000);
    private AtomicBoolean mIsPaused = new AtomicBoolean(false);

    Context context;
    SurfaceHolder mHolder;
    private boolean limit = false;

    public boolean isLimit() {
        return limit;
    }

    public void setLimit(boolean limit) {
        this.limit = limit;
    }

    public DrawingBoardSurfaceView(Context context) {
        super(context);

        this.context = context;

        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public DrawingBoardSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHolder = getHolder();

        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsPaused.set(true);
    }

    public void setAliveHintTextView(TextView textView) {
        aliveHintTextView = textView;
    }

    public void setGenerationTextView(TextView textView) {
        generationTextView = textView;
    }

    public void initGame() {
        num = 100;
        initGame(new Creatures(num));
    }

    public void initGame(String mode) {
        num = 100;
        int[][] matrix = StatusFactory.getSampleStatus(mode);
        initGame(new Creatures(num, matrix));
    }

    public void initGame(int[][] matrix) {
        num = matrix.length;
        initGame(new Creatures(matrix.length, matrix));
    }

    private void initGame(Creatures c) {
        int viewHeight = getHeight(), viewWidth = getWidth();
        mWidth = viewWidth < viewHeight ? viewWidth : viewHeight;
        mGridUnit = mWidth / num;

        mIsPaused.set(true);
        creatures = c;
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        Canvas canvas = mHolder.lockCanvas();
        draw(canvas, paint);
        mHolder.unlockCanvasAndPost(canvas);

        aliveHintTextView.setText(String.valueOf(creatures.getAliveNum()));
        generationTextView.setText(String.valueOf(creatures.getGeneration()));
    }

    public void pauseGame() {
        mIsPaused.set(true);
    }

    public void continueGame() {
        mIsPaused.set(false);
        new Thread(new DrawThread()).start();
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime.getAndSet(sleepTime);
    }

    private void draw(Canvas canvas, Paint paint) {
        int[][] livingStatus = creatures.getLivingStatus();

        paint.setColor(Color.BLACK);
        filledSquare(canvas, paint, mWidth / 2, mWidth / 2, mWidth / 2);

        // draw n-by-n grid
        for (int row = 0; row < num; row++) {
            for (int col = 0; col < num; col++) {
                if (livingStatus[row][col] == 0) {
                    continue;
                } else if (livingStatus[row][col] == 1) {
                    paint.setColor(Color.WHITE);
                } else if (livingStatus[row][col] == 2) {
                    paint.setColor(Color.CYAN);
                } else if (livingStatus[row][col] == 3) {
                    paint.setColor(Color.GREEN);
                } else {
                    paint.setColor(Color.YELLOW);
                }
                filledSquare(canvas, paint, (col + 0.5f) * mGridUnit,
                        (row + 0.5f) * mGridUnit, 0.45f * mGridUnit);
            }
        }
    }

    private void filledSquare(Canvas canvas, Paint paint, float x, float y, float halfLength) {
        RectF rect = new RectF();
        rect.set(x - halfLength, y + halfLength,
                x + halfLength, y - halfLength);
        canvas.drawRect(rect, paint);
    }


    class DrawThread implements Runnable {
        @Override
        public void run() {

            Canvas canvas = null;
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);

            while (!mIsPaused.get()) {
                try {
                    creatures.setLimit(limit);
                    creatures.nextTime();
                    if (mHolder.getSurface().isValid()) {
                        canvas = mHolder.lockCanvas();
                    }
                    if (canvas != null && canvas.getWidth() > 0) {
                        draw(canvas, paint);
                        Message msg = new Message();
                        int[] data = {creatures.getAliveNum(), creatures.getGeneration()};
                        msg.obj = data;
                        mHandler.sendMessage(msg);
                    }
                    Thread.sleep(sleepTime.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (mHolder.getSurface().isValid()) {
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }


}
