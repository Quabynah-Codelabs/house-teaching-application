package io.codelabs.digitutor.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.base.BaseActivity;
import io.codelabs.digitutor.view.adapter.SubjectAdapter;
import io.codelabs.recyclerview.SlideInItemAnimator;

public class AddSubjectActivity extends BaseActivity {
    @BindView(R.id.subjects_grid)
    public RecyclerView grid;
    @BindView(R.id.app_bar)
    public AppBarLayout appBar;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    public CollapsingToolbarLayout collapsingToolbar;

    private SubjectAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        ButterKnife.bind(this);

        adapter =  new SubjectAdapter(this);
        grid.setAdapter(adapter);
        grid.setLayoutManager(new LinearLayoutManager(this));
        grid.setHasFixedSize(true);
        grid.setItemAnimator(new SlideInItemAnimator());

        loadData();
    }

    private void loadData() {
        // TODO: 001 01.05.19 Fetch data and add it to the Grid
    }
}
