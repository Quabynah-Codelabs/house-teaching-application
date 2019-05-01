package io.codelabs.digitutor.view;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.transition.TransitionManager;

import java.util.Objects;

import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.base.BaseActivity;
import io.codelabs.digitutor.core.datasource.remote.FirebaseDataSource;
import io.codelabs.digitutor.core.datasource.remote.LoginCredentials;
import io.codelabs.digitutor.core.util.AsyncCallback;
import io.codelabs.digitutor.core.util.Constants;
import io.codelabs.digitutor.data.BaseUser;
import io.codelabs.digitutor.databinding.ActivityLoginBinding;
import io.codelabs.sdk.util.ExtensionUtils;

public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding binding;
    public static final String EXTRA_USER_TYPE = "EXTRA_USER_TYPE";
    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        // Hide Loading view
        binding.loading.setVisibility(View.GONE);

        // Get the user type from the intent bundle
        type = getIntent().hasExtra(EXTRA_USER_TYPE) ? getIntent().getStringExtra(EXTRA_USER_TYPE) : BaseUser.Type.PARENT;
    }

    public void createNewAccount(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(RegisterActivity.EXTRA_USER_TYPE, type);
        intentTo(RegisterActivity.class,bundle, true);
    }

    public void resetPassword(View view) {
        intentTo(ResetPassword.class);
    }

    public void loginUser(View view) {
        String email = Objects.requireNonNull(binding.email.getText()).toString();
        String password = Objects.requireNonNull(binding.password.getText()).toString();

        FirebaseDataSource.login(this, auth,
                new LoginCredentials(email, password), new AsyncCallback<Void>() {
                    @Override
                    public void onError(@Nullable String error) {
                        ExtensionUtils.toast(LoginActivity.this, error, true);
                    }

                    @Override
                    public void onSuccess(@Nullable Void response) {
                        ExtensionUtils.showConfirmationToast(LoginActivity.this, Constants.DEFAULT_AVATAR_URL,
                                auth.getCurrentUser().getEmail() != null ? auth.getCurrentUser().getEmail() : auth.getCurrentUser().getUid(),
                                "Logged in as...");

                        // Store user data locally
                        prefs.login(auth.getUid(), type);

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
