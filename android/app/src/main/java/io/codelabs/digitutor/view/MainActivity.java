package io.codelabs.digitutor.view;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

import io.codelabs.digitutor.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the Firebase Name: should be [DEFAULT] if initialized properly
        ((TextView) findViewById(R.id.test_firebase)).setText(FirebaseApp.initializeApp(this).getName());
    }
}
