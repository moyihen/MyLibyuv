package com.moyihen.mylibyuv

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.moyihen.mylibyuv.databinding.ActivityFunctionBinding

class FunctionAct : AppCompatActivity() {

    lateinit var mBinding: ActivityFunctionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_function)

        mBinding = ActivityFunctionBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        initView()
    }

    private fun initView() {

        mBinding.btn1.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }


        mBinding.btn2.setOnClickListener {
            startActivity(Intent(this, TestAct::class.java))
        }

        mBinding.btn3.setOnClickListener {
            startActivity(Intent(this, Test2::class.java))
        }
        //烟花
        mBinding.btn4.setOnClickListener {
            //startActivity(Intent(this,Test2::class.java))
        }
        //camerax自定义裁剪
        mBinding.btn5.setOnClickListener {
            startActivity(Intent(this,CameraAct::class.java))
        }
    }
}