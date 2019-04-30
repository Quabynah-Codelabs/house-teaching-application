package io.codelabs.digitutor.view;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.base.BaseActivity;
import io.codelabs.digitutor.databinding.ActivityRegisterBinding;

public class RegisterActivity extends BaseActivity {
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        binding.loading.setVisibility(View.GONE);
    }

    public void createUser(View view) {

    }

    public void navLogin(View view) {
        intentTo(LoginActivity.class, true);
    }

    @Override
    public void onBackPressed() {
        navLogin(null);
    }
}
