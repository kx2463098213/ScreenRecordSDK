package com.orz.record;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
//        ATest.init(this);
        initVar();
    }

    private void initVar(){
        mBinder = ButterKnife.bind(this);
//        ATest.init(getApplicationContext());
    }

    @OnClick({R.id.btn_record})
    void onClickEvent(View view){
        int id = view.getId();
        switch (id){
            case R.id.btn_record:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBinder != null){
            mBinder.unbind();
        }
    }
}
