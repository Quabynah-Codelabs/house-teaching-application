package io.codelabs.digitutor.view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.base.BaseActivity;
import io.codelabs.digitutor.databinding.ActivityComplaintBinding;

public class ComplaintActivity extends BaseActivity {
    public static final String EXTRA_COMPLAINT = "extra_complaint";
    private ActivityComplaintBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complaint);
    }
}
