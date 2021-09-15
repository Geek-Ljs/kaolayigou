package com.shoppingmail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Random;

/**
 * Created by lam00 on 2019/6/22
 */

public class InitAdvActivity extends BaseActivity {
    private int[] picsLayout = {R.layout.layout_hello, R.layout.layout_hello2,
            R.layout.layout_hello3, };
    private int i;
    private int count = 3;
    private Button mBtnSkip;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                mBtnSkip.setText("ENTER (" + getCount() + ")");
                handler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };
    private LinearLayout linearLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Random r = new Random();
        i = r.nextInt(3);
        Log.i("test", "随机数是:" + i);
        if (i < 3) {
            //随机概率会出现广告页
            setContentView(R.layout.activity_adv);
            initView2();
        } else {
            startActivity(new Intent(InitAdvActivity.this, RecommendActivity.class));
            finish();
        }
    }

    private void initView2() {
        linearLayout = findViewById(R.id.advLine);
        View view = View.inflate(InitAdvActivity.this, picsLayout[i], null);
        linearLayout.addView(view);
        mBtnSkip = view.findViewById(R.id.btn_skip);
        mBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InitAdvActivity.this, RecommendActivity.class));
                handler.removeMessages(0);
                finish();
            }
        });
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    public int getCount() {
        count--;
        if (count == 0) {
            Intent intent = new Intent(this, RecommendActivity.class);
            startActivity(intent);
            finish();
        }
        return count;
    }
}