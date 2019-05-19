package io.codelabs.digitutor.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.base.BaseActivity;
import io.codelabs.digitutor.data.model.Tutor;
import io.codelabs.digitutor.databinding.ActivitySchedulesBinding;
import io.codelabs.sdk.util.ExtensionUtils;

public class SchedulesActivity extends BaseActivity {
    public static final String EXTRA_TUTOR = "EXTRA_TUTOR";
    public static final String EXTRA_TUTOR_ID = "EXTRA_TUTOR_ID";

    private ActivitySchedulesBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedules);
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Intent i = getIntent();
        if (i.hasExtra(EXTRA_TUTOR)) {
            binding.setIsTutor(i.getParcelableExtra(EXTRA_TUTOR) instanceof Tutor);
        } else if (i.hasExtra(EXTRA_TUTOR_ID))
            binding.setIsTutor(i.getStringExtra(EXTRA_TUTOR_ID) != null && !TextUtils.isEmpty(i.getStringExtra(EXTRA_TUTOR_ID)));

        ExtensionUtils.debugLog(getApplicationContext(), "User is tutor: " + binding.getIsTutor());
    }
}
