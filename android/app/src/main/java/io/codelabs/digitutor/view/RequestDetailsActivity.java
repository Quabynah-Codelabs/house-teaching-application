package io.codelabs.digitutor.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.base.BaseActivity;
import io.codelabs.digitutor.core.datasource.remote.FirebaseDataSource;
import io.codelabs.digitutor.core.util.AsyncCallback;
import io.codelabs.digitutor.data.BaseUser;
import io.codelabs.digitutor.data.model.Request;
import io.codelabs.digitutor.databinding.ActivityRequestDetailsBinding;
import io.codelabs.sdk.util.ExtensionUtils;

public class RequestDetailsActivity extends BaseActivity {
    public static final String EXTRA_REQUEST_ID = "EXTRA_REQUEST_ID";
    public static final String EXTRA_REQUEST_PARENT = "EXTRA_REQUEST_PARENT";

    private ActivityRequestDetailsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_details);

        if (getIntent().hasExtra(EXTRA_REQUEST_ID)) {
            FirebaseDataSource.getCurrentRequest(this, firestore, getIntent().getStringExtra(EXTRA_REQUEST_ID), new AsyncCallback<Request>() {
                @Override
                public void onError(@Nullable String error) {
                    ExtensionUtils.toast(RequestDetailsActivity.this, error, true);
                }

                @Override
                public void onSuccess(@Nullable Request response) {
                    ExtensionUtils.debugLog(RequestDetailsActivity.this, response);
                    if (response != null) binding.setRequest(response);
                }

                @Override
                public void onStart() {
                    // TODO: 006 06.05.19 Show loading
                }

                @Override
                public void onComplete() {
                    // TODO: 006 06.05.19 Hide loading
                }
            });

            if (getIntent().hasExtra(EXTRA_REQUEST_PARENT)) {
                FirebaseDataSource.getUser(this, firestore, getIntent().getStringExtra(EXTRA_REQUEST_PARENT), BaseUser.Type.PARENT, new AsyncCallback<BaseUser>() {
                    @Override
                    public void onError(@Nullable String error) {

                    }

                    @Override
                    public void onSuccess(@Nullable BaseUser response) {
                        ExtensionUtils.debugLog(RequestDetailsActivity.this, response);
                        if (response != null) binding.setUser(response);
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

    }
}
