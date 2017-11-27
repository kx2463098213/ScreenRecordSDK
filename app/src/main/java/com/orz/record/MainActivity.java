package com.orz.record;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.orz.recorder.ATest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    private Unbinder mBinder;

    @BindView(R.id.btn_record)
    Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVar();
    }

    private void initVar(){
        mBinder = ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_record})
    void onClickEvent(View view){
        int id = view.getId();
        switch (id){
            case R.id.btn_record:
                showSomething();
                break;
        }
    }

    private void showSomething(){
        Toast.makeText(this, "Don't touch me pls.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBinder != null){
            mBinder.unbind();
        }
    }
}
