package io.codelabs.digitutor.view;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.base.BaseActivity;
import io.codelabs.digitutor.databinding.ActivityResetBinding;

public class ResetPassword extends BaseActivity {
    private ActivityResetBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_reset);

        binding.loading.setVisibility(View.GONE);

    }

    public void resetUserPassword(View view) {
        // todo: reset user password here
    }

    public void navLogin(View view) {
        intentTo(LoginActivity.class, true);
    }
}
