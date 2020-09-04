package cn.wzbos.chihiro.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.wzbos.chihiro.library1.Test1;


public class MainActivity extends AppCompatActivity {

    TextView txtSay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtSay = findViewById(R.id.txtSay);
        findViewById(R.id.btnTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtSay.setText(Test1.say());
            }
        });


    }
}