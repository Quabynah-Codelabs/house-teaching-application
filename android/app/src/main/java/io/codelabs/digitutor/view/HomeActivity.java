package io.codelabs.digitutor.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.databinding.DataBindingUtil;

import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.base.BaseActivity;
import io.codelabs.digitutor.databinding.ActivityHomeBinding;

/**
 * Home activity class
 */
public class HomeActivity extends BaseActivity {
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawer, binding.bottomAppBar, R.string.open_drawer, R.string.close_drawer);
        binding.drawer.addDrawerListener(toggle);
        toggle.syncState();

        //todo: setup header
        //setupHeaderView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // TODO: 030 30.04.19 Send device token to server
    }
}
