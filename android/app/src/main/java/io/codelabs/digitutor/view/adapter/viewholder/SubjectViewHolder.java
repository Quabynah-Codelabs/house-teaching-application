package io.codelabs.digitutor.view.adapter.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.codelabs.digitutor.R;
import io.codelabs.widget.BaselineGridTextView;

/**
 * ViewHolder subclass for showing all subjects
 */
public class SubjectViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.subject_name)
    public BaselineGridTextView name;
    @BindView(R.id.subject_desc)
    public BaselineGridTextView desc;

    public SubjectViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
