package io.codelabs.digitutor.view;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.transition.TransitionManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.base.BaseActivity;
import io.codelabs.digitutor.core.datasource.FirebaseDataSource;
import io.codelabs.digitutor.core.datasource.LoginCredentials;
import io.codelabs.digitutor.core.util.AsyncCallback;
import io.codelabs.digitutor.data.BaseUser;
import io.codelabs.digitutor.databinding.ActivityLoginBinding;
import io.codelabs.sdk.util.ExtensionUtils;

public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding binding;
    public static final String EXTRA_USER_TYPE = "EXTRA_USER_TYPE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        // Hide Loading view
        binding.loading.setVisibility(View.GONE);
    }

    public void createNewAccount(View view) {
        intentTo(RegisterActivity.class, true);
    }

    public void resetPassword(View view) {
        intentTo(ResetPassword.class);
    }

    public void loginUser(View view) {
        String email = ((EditText) findViewById(R.id.email)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();

        FirebaseDataSource.login(this, FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(),
                new LoginCredentials(email, password), BaseUser.Type.PARENT, new AsyncCallback<Void>() {
                    @Override
                    public void onError(@Nullable String error) {
                        ExtensionUtils.toast(LoginActivity.this, error, true);
                    }

                    @Override
                    public void onSuccess(@Nullable Void response) {
                        ExtensionUtils.toast(LoginActivity.this, "Logged in successfully", true);
                        intentTo(HomeActivity.class, true);
                    }

                    @Override
                    public void onStart() {
                        TransitionManager.beginDelayedTransition(binding.container);
                        binding.loading.setVisibility(View.VISIBLE);
                        binding.content.setVisibility(View.GONE);
                    }

                    @Override
                    public void onComplete() {
                        TransitionManager.beginDelayedTransition(binding.container);
                        binding.loading.setVisibility(View.GONE);
                        binding.content.setVisibility(View.VISIBLE);
                    }
                });
    }
}
