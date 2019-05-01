package io.codelabs.digitutor.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.navigation.NavigationView;

import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.base.BaseActivity;
import io.codelabs.digitutor.core.datasource.remote.FirebaseDataSource;
import io.codelabs.digitutor.core.util.AsyncCallback;
import io.codelabs.digitutor.core.util.Constants;
import io.codelabs.digitutor.data.BaseUser;
import io.codelabs.digitutor.databinding.ActivityHomeBinding;
import io.codelabs.sdk.glide.GlideApp;
import io.codelabs.sdk.util.ExtensionUtils;
import io.codelabs.widget.CircularImageView;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Home activity class
 */
public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        setSupportActionBar(binding.bottomAppBar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawer, binding.bottomAppBar, R.string.open_drawer, R.string.close_drawer);
        binding.drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Sync navigation view state
        setupHeaderView();
        binding.navView.setNavigationItemSelectedListener(this);

        // Set FAB icon & click action
        if (BaseUser.Type.PARENT.equals(prefs.getType())) {
            binding.fab.setImageDrawable(getResources().getDrawable(R.drawable.twotone_supervisor_account_24px));
            binding.fab.setOnClickListener(v -> {
                // TODO: 001 01.05.19 Add new ward
            });
        } else {
            binding.fab.setImageDrawable(getResources().getDrawable(R.drawable.twotone_assignment_24px));
            binding.fab.setOnClickListener(v -> {
                // TODO: 001 01.05.19 Add new ward
            });
        }
    }

    private void setupHeaderView() {
        // Get fields from header view
        View headerView = binding.navView.getHeaderView(0);
        CircularImageView avatar = headerView.findViewById(R.id.header_avatar);
        TextView username = headerView.findViewById(R.id.header_username);
        TextView type = headerView.findViewById(R.id.header_user_type);

        username.setText(prefs.getKey());
        type.setText(String.format("Logged in as: %s", prefs.getType()));

        // Get live data from database
        FirebaseDataSource.getCurrentUser(this, firestore, prefs, new AsyncCallback<BaseUser>() {
            @Override
            public void onError(@Nullable String error) {
                ExtensionUtils.toast(HomeActivity.this, error, true);
            }

            @Override
            public void onSuccess(@Nullable BaseUser response) {
                if (response == null) {
                    ExtensionUtils.toast(HomeActivity.this, "Cannot find this user. PLease re-authenticate this account", true);
                    return;
                }

                // Load user's profile image with Glide
                GlideApp.with(HomeActivity.this)
                        .load(response.getAvatar() == null ? Constants.DEFAULT_AVATAR_URL : response.getAvatar())
                        .circleCrop()
                        .placeholder(R.drawable.avatar_placeholder)
                        .error(R.drawable.ic_player)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .transition(withCrossFade())
                        .into(avatar);

                username.setText(response.getName());
                type.setText(String.format("Logged in as: %s", response.getType().toLowerCase()));

                ExtensionUtils.debugLog(HomeActivity.this, response);
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete() {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        // TODO: 030 30.04.19 Send device token to server
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(prefs.getType().equals(BaseUser.Type.PARENT) ? R.menu.parent_bottom_bar_menu : R.menu.tutor_bottom_bar_menu, menu);
        if (menu instanceof MenuBuilder) ((MenuBuilder) menu).setOptionalIconsVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                // TODO: 001 01.05.19 Show Search screen
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_logout:
                // TODO: 001 01.05.19 logout user here
                break;
            case R.id.menu_tutor_feedback:
                // TODO: 001 01.05.19 Show Tutor feedback fragment
                break;
            case R.id.menu_view_timetable:
                // TODO: 001 01.05.19 Show Timetable fragment
                break;
        }

        binding.drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }
}
