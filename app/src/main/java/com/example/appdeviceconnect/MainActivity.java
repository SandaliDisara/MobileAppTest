package com.example.appdeviceconnect;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a new TextView programmatically
        TextView textView = new TextView(this);
        textView.setText("Hello, World!");
        textView.setTextSize(30); // Set the text size
        textView.setPadding(20, 100, 20, 20); // Padding for better visual

        // Set the TextView as the main view
        setContentView(textView);
    }
}