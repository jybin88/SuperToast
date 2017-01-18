package io.github.toast;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.lfh.custom.widget.toast.ToastUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_system_toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                Toast.makeText(MainActivity.this, "system toast", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn_super_toast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                ToastUtil.getInstance().toast(MainActivity.this, "super toast");
            }
        });
    }
}
