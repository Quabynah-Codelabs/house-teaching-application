package io.codelabs.digitutor.view;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.base.BaseActivity;
import io.codelabs.digitutor.core.datasource.remote.FirebaseDataSource;
import io.codelabs.digitutor.core.util.AsyncCallback;
import io.codelabs.digitutor.data.BaseUser;
import io.codelabs.digitutor.data.model.Subject;
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

        if (getIntent().hasExtra(EXTRA_USER)) {
            binding.setUser(getIntent().getParcelableExtra(EXTRA_USER));
            ExtensionUtils.debugLog(this, binding.getUser().getKey());
            getRequest(binding.getUser().getKey());
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

                    if (response == null) return;
                    getRequest(response.getKey());
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

    /**
     * Check whether or not user has already sent a request to the tutor
     *
     * @param key The tutor's key
     */
    private void getRequest(String key) {
        // Snackbar to show notification on the screen for the current user
        Snackbar snackbar = Snackbar.make(binding.container, "", Snackbar.LENGTH_LONG);

        FirebaseDataSource.getSentRequests(UserActivity.this, firestore, prefs, key, new AsyncCallback<Boolean>() {
            @Override
            public void onError(@Nullable String error) {
                snackbar.setText(error == null ? "An unknown error occurred" : error).show();
            }

            @Override
            public void onSuccess(@Nullable Boolean response) {
                ExtensionUtils.debugLog(UserActivity.this, response);
                binding.requestButton.setEnabled(response != null && response);

                if (binding.getUser().getType().equals(BaseUser.Type.TUTOR)) {
                    // Get all subjects for this tutor
                    FirebaseDataSource.getTutorSubjects(UserActivity.this, firestore, key, new AsyncCallback<List<Subject>>() {
                        @Override
                        public void onError(@Nullable String error) {
                            snackbar.setText(error == null ? "An unknown error occurred" : error).show();
                        }

                        @Override
                        public void onSuccess(@Nullable List<Subject> response) {
                            if (response != null) {
                                // TODO: 007 07.05.19 Add the subjects to the list of subjects for this tutor
                            }
                        }

                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
                }
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete() {

            }
        });
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
                    ExtensionUtils.toast(getApplicationContext(), "Request completed", false);
                }
            });
            finish();
        } else {
            ExtensionUtils.toast(this, "Cannot send request. Please make sure that this request is sent to a tutor instead", true);
        }
    }
}
