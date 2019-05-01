package io.codelabs.digitutor.view;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.databinding.DataBindingUtil;

import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.base.BaseActivity;
import io.codelabs.digitutor.databinding.ActivityHomeBinding;
import io.codelabs.widget.BaselineGridTextView;
import io.codelabs.widget.CircularImageView;

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

        setupHeaderView();
    }

    private void setupHeaderView() {
        // TODO: 001 01.05.19 Get user information from the database

        // Get fields from header view
        View headerView = binding.navView.getHeaderView(0);
        CircularImageView avatar = headerView.findViewById(R.id.header_avatar);
        BaselineGridTextView username = headerView.findViewById(R.id.header_username);
        BaselineGridTextView type = headerView.findViewById(R.id.header_user_type);

        // TODO: 001 01.05.19 Bind properties
    }

    @Override
    protected void onStart() {
        super.onStart();
        // TODO: 030 30.04.19 Send device token to server
    }
}
