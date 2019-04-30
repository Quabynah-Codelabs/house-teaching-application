package io.codelabs.digitutor.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.base.BaseActivity;
import io.codelabs.digitutor.databinding.ActivityRegisterBinding;
import io.codelabs.sdk.glide.GlideApp;

public class RegisterActivity extends BaseActivity {
    public static final String EXTRA_USER_TYPE = "EXTRA_USER_TYPE";
    private static final int RC_PROFILE = 3;
    private ActivityRegisterBinding binding;

    // Image url
    private Uri imageUrl = Uri.EMPTY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        binding.loading.setVisibility(View.GONE);

        binding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"});
            startActivityForResult(intent, RC_PROFILE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PROFILE && resultCode == RESULT_OK) {
            imageUrl = data.getData();

            GlideApp.with(this)
                    .load(imageUrl)
                    .circleCrop()
                    .into(binding.profile);
        }
    }

    public void createUser(View view) {
        if (imageUrl != null) {
            // TODO: 030 30.04.19 Upload image and get download uri
        } else {
            // TODO: 030 30.04.19 Just update user
        }
    }

    public void navLogin(View view) {
        intentTo(LoginActivity.class, true);
    }

    @Override
    public void onBackPressed() {
        navLogin(null);
    }
}
