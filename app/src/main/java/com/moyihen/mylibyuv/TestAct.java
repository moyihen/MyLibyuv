package com.moyihen.mylibyuv;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.moyihen.mylibyuv.databinding.ActivityTestBinding;

public class TestAct extends AppCompatActivity {

    private ActivityTestBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        //进度条
        mBinding.bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.custompgb.startAni(270);
            }
        });
    }
}