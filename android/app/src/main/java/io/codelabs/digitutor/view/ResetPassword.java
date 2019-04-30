package io.codelabs.digitutor.view;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.base.BaseActivity;

public class ResetPassword extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);


    }

    public void resetUserPassword(View view) {
        // todo: reset user password here
    }

    public void navLogin(View view) {
        intentTo(LoginActivity.class, true);
    }
}
