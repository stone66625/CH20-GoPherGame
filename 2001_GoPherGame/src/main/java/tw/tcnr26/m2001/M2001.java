package tw.tcnr26.m2001;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class M2001 extends AppCompatActivity {

    private ImageView[] imageViewList;
    private TextView textView;
    private Button btnPlay;
    private int[] goPher;
    private Handler handler;
    private GopherSprite[] glist;
    private boolean play = false;
    private int score = 0;
    private SoundPool soundPool;
    private int touchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m2001);
        setupViewComponent();
    }

    private void setupViewComponent() {
        imageViewList = new ImageView[]{
                (ImageView) findViewById(R.id.imageView1),
                (ImageView) findViewById(R.id.imageView2),
                (ImageView) findViewById(R.id.imageView3),
                (ImageView) findViewById(R.id.imageView4),
                (ImageView) findViewById(R.id.imageView5),
                (ImageView) findViewById(R.id.imageView6),
                (ImageView) findViewById(R.id.imageView7),
                (ImageView) findViewById(R.id.imageView8),
                (ImageView) findViewById(R.id.imageView9),
                (ImageView) findViewById(R.id.imageView10),
                (ImageView) findViewById(R.id.imageView11),
                (ImageView) findViewById(R.id.imageView12)
        };
        textView = (TextView)findViewById(R.id.tscore);
        btnPlay = (Button)findViewById(R.id.btn_play);
        //建立地鼠循環陣列
        goPher = new int[]{
          R.drawable.mole1, R.drawable.mole2, R.drawable.mole3, R.drawable.mole4,
                R.drawable.mole3, R.drawable.mole2, R.drawable.mole1, R.drawable.hole
        };

        handler = new Handler();
        //建立音效池
        buildSoundPool();
        // 建立地鼠遊戲物件與註冊 onTouch 監聽器
        glist = new GopherSprite[12];
        for (int i = 0; i < glist.length; i++) {
            glist[i] = new GopherSprite(imageViewList[i]);
            imageViewList[i].setOnTouchListener(new GopherOnTouchListener(glist[i]));
        }

    }

    private void buildSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(attr)
                    .build();
        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        }
        touchId = soundPool.load(this, R.raw.touch, 1);
    }

    public void btn_play_Click(View view) {
        play = true;
        score = 0;
        textView.setText("0");
        btnPlay.setText("遊戲進行中");
        btnPlay.setEnabled(false);

        new CountDownTimer(30000, 1000) { //設定一局 30秒
            @Override
            public void onFinish() {
                play = false;
                setTitle("剩餘時間：0");
                btnPlay.setText("開始遊戲");
                btnPlay.setEnabled(true);
            }

            @Override
            public void onTick(long millisUntilFinished) {
                setTitle("剩餘時間：" + millisUntilFinished / 1000);
            }
        }.start();
        for (GopherSprite g : glist) {
            handler.post(g);
        }
    }

    //===========================================inner class==============================================
    private class GopherSprite implements Runnable {

        ImageView imageView;
        int idx;
        boolean hit;

        GopherSprite(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        public void run() {
            draw();
        }

        private void draw() {
            if (!play) {
                return;
            }
            if (hit) {
                imageView.setImageResource(R.drawable.mole5);
                hit = false;
                idx = 0;
                handler.postDelayed(this, 1000);
            } else {
                idx = idx % goPher.length;
                imageView.setImageResource(goPher[idx]);
                int n = (int) (Math.random() * 1000) % 9 + 1;
                handler.postDelayed(this, (n * 15)); //探頭停頓時間
                idx = ++idx % goPher.length;
            }
        }
    }

    private class GopherOnTouchListener implements View.OnTouchListener {
        GopherSprite g;

        GopherOnTouchListener(GopherSprite g) {
            this.g = g;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (play && event.getAction() == MotionEvent.ACTION_DOWN) {
                if (goPher[g.idx] == R.drawable.mole2 ||
                        goPher[g.idx] == R.drawable.mole3 ||
                        goPher[g.idx] == R.drawable.mole4) {
                    g.hit = true;
                    soundPool.play(touchId, 1.0F, 1.0F, 0, 0, 1.0F);
                    textView.setText(String.valueOf(score = score +10));
                } else {
                    textView.setText(String.valueOf(--score));
                }
            }
            return false;
        }


    }

}