package com.example.tfliteandroid;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.divyanshu.draw.widget.DrawView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private DrawView drawView;
    private Button clearBtn;
    private TextView text;
    private DigitClassifier digitClassifier = new DigitClassifier(this);

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawView = findViewById(R.id.draw_view);
        if(drawView != null){
            drawView.setStrokeWidth(70f);
            drawView.setColor(Color.WHITE);
            drawView.setBackgroundColor(Color.BLACK);
        }
        clearBtn = findViewById(R.id.clearBtn);
        text = findViewById(R.id.text);

        clearBtn.setOnClickListener(event -> {
            drawView.clearCanvas();
            text.setText("Please draw a digit");
        });

        drawView.setOnTouchListener((view, motionEvent) -> {
            drawView.onTouchEvent(motionEvent);
            if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                classifyDrawing();
            }
            return true;
        });
        digitClassifier
                .initialize()
                .addOnFailureListener(e -> Log.e(TAG, "Error to setting up digit classifier.", e));
    }

    @SuppressLint("SetTextI18n")
    private void classifyDrawing() {
        Bitmap bitmap = drawView.getBitmap();
        if(digitClassifier.isInitialized()){
            digitClassifier
                    .classifyAsync(bitmap)
                    .addOnSuccessListener(resultText -> text.setText(resultText))
                    .addOnFailureListener(e -> {
                        text.setText("Error");
                        Log.e(TAG, "Error classifying drawing.", e);
                    });
        }
    }

    @Override
    protected void onDestroy() {
        digitClassifier.close();
        super.onDestroy();
    }
}
