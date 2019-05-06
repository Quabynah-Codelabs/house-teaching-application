package io.codelabs.digitutor.view;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.base.BaseActivity;
import io.codelabs.digitutor.core.datasource.remote.FirebaseDataSource;
import io.codelabs.digitutor.core.util.AsyncCallback;
import io.codelabs.digitutor.data.BaseUser;
import io.codelabs.digitutor.data.model.Tutor;
import io.codelabs.digitutor.databinding.ActivityUserBinding;
import io.codelabs.sdk.util.ExtensionUtils;

public class UserActivity extends BaseActivity {
    public static final String EXTRA_USER = "EXTRA_USER";
    public static final String EXTRA_USER_UID = "EXTRA_USER_UID";
    public static final String EXTRA_USER_TYPE = "EXTRA_USER_TYPE";

    private ActivityUserBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user);
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        binding.setPrefs(prefs);

        if (getIntent().hasExtra(EXTRA_USER)) {
            binding.setUser(getIntent().getParcelableExtra(EXTRA_USER));
        } else if (getIntent().hasExtra(EXTRA_USER_UID)) {
            Snackbar snackbar = Snackbar.make(binding.container, "Fetching user information", Snackbar.LENGTH_INDEFINITE);
            FirebaseDataSource.getUser(this, firestore, getIntent().getStringExtra(EXTRA_USER_UID), getIntent().getStringExtra(EXTRA_USER_TYPE), new AsyncCallback<BaseUser>() {
                @Override
                public void onError(@Nullable String error) {
                    snackbar.setText(error == null ? "An error occurred while retrieving user information" : error);
                    snackbar.setDuration(BaseTransientBottomBar.LENGTH_LONG);
                }

                @Override
                public void onSuccess(@Nullable BaseUser response) {
                    binding.setUser(response);
                }

                @Override
                public void onStart() {
                    snackbar.show();
                }

                @Override
                public void onComplete() {
                    snackbar.dismiss();
                }
            });
        }


    }

    public void requestService(View view) {
        if (binding.getUser() != null && binding.getUser() instanceof Tutor) {
            ExtensionUtils.toast(getApplicationContext(), "Sending request", false);
            FirebaseDataSource.requestService(firestore, prefs, binding.getUser().getKey(), new AsyncCallback<Void>() {
                @Override
                public void onError(@Nullable String error) {
                    ExtensionUtils.toast(getApplicationContext(), error, true);
                }

                @Override
                public void onSuccess(@Nullable Void response) {
                    ExtensionUtils.toast(getApplicationContext(), "Request sent successfully", false);
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onComplete() {
                    ExtensionUtils.debugLog(getApplicationContext(), "Request completed");
                }
            });
            finish();
        } else {
            ExtensionUtils.toast(this, "Cannot send request. Please make sure that this request is sent to a tutor instead", true);
        }
    }
}
