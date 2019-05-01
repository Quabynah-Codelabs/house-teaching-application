package io.codelabs.digitutor.view.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.codelabs.digitutor.data.model.Subject;

/**
 * {@link androidx.recyclerview.widget.RecyclerView.Adapter} subclass for all {@linkplain Subject}s
 */
public class SubjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Subject> subjects = new ArrayList<>(0);

    public static final int TYPE_EMPTY = -1;
    public static final int TYPE_SUBJECT = 0;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }



    @Override
    public int getItemCount() {
        return 0;
    }
}
