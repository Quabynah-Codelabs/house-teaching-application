package io.codelabs.digitutor.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.base.BaseActivity;
import io.codelabs.digitutor.core.datasource.remote.FirebaseDataSource;
import io.codelabs.digitutor.core.util.AsyncCallback;
import io.codelabs.digitutor.data.model.Subject;
import io.codelabs.digitutor.databinding.ActivitySubjectBinding;
import io.codelabs.digitutor.view.adapter.SubjectAdapter;
import io.codelabs.recyclerview.GridItemDividerDecoration;
import io.codelabs.recyclerview.SlideInItemAnimator;
import io.codelabs.sdk.util.ExtensionUtils;

public class AddSubjectActivity extends BaseActivity {
    private ActivitySubjectBinding binding;
    private SubjectAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_subject);
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        adapter = new SubjectAdapter(this);
        binding.subjectsGrid.setAdapter(adapter);
        binding.subjectsGrid.setLayoutManager(new LinearLayoutManager(this));
        binding.subjectsGrid.setHasFixedSize(true);
        binding.subjectsGrid.setItemAnimator(new SlideInItemAnimator());
        binding.subjectsGrid.addItemDecoration(new GridItemDividerDecoration(this, R.dimen.divider_height, R.color.divider));
        loadData();
    }

    private void loadData() {
        FirebaseDataSource.fetchAllSubjects(this, firestore, new AsyncCallback<List<Subject>>() {
            @Override
            public void onError(@Nullable String error) {
                ExtensionUtils.toast(AddSubjectActivity.this, error, true);
            }

            @Override
            public void onSuccess(@Nullable List<Subject> response) {
                if (response != null) adapter.addData(response);
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
