package io.codelabs.digitutor.view.adapter.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.codelabs.digitutor.R;
import io.codelabs.widget.ForegroundImageView;

/**
 * ViewHolder subclass for showing empty state of a process
 */
public class EmptyViewHolder extends RecyclerView.ViewHolder {
    public ForegroundImageView imageView;

    public EmptyViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.empty_view_image);
    }
}
