package io.codelabs.digitutor.view;

import android.os.Bundle;

import androidx.annotation.Nullable;

import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.base.BaseActivity;

/**
 * Search screen
 */
public class AssignmentActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment);
    }
}