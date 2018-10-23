package hy.h_bezier_dragbubble;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class Main2Activity extends Activity {
    private LinearLayout ll_content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2main);
        float d=(float) Math.hypot(480,320)/576;
        Log.e("msg","======hh====>"+d);
        ll_content = findViewById(R.id.ll_content);
    }

    public void two(View view) {
        ll_content.removeAllViews();
        ll_content.addView(new TwoBesselView(this));

    }

    public void three(View view) {
        ll_content.removeAllViews();
        ll_content.addView(new ThreeBesselView(this));

    }

    public void other(View view) {
        ll_content.removeAllViews();
        ll_content.addView(new OtherBesselView(this));

    }

    public void bubble(View view) {
        startActivity(new Intent(this, Main22Activity.class));
    }


}
