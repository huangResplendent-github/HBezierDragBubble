package hy.h_bezier_dragbubble;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class Main22Activity extends Activity {
    private DragBubbleView bubbleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main22);
        bubbleView = findViewById(R.id.drag_bubble_view);

    }

    public void reset(View view) {
        bubbleView.reset();
    }
}
