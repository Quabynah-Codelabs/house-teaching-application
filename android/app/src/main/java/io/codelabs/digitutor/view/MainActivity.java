package io.codelabs.digitutor.view;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void showLoginDialog(View view) {
        //todo: show login dialog
        intentTo(HomeActivity.class, true);
    }
}
