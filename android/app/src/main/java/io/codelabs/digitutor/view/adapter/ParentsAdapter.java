package io.codelabs.digitutor.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.util.OnClickListener;
import io.codelabs.digitutor.data.model.Parent;
import io.codelabs.digitutor.view.adapter.viewholder.EmptyViewHolder;
import io.codelabs.digitutor.view.adapter.viewholder.UserViewHolder;
import io.codelabs.sdk.glide.GlideApp;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ParentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Parent> parents = new ArrayList<>(0);
    private static final int TYPE_EMPTY = R.layout.item_empty;
    private static final int TYPE_USER = R.layout.item_user;

    private final LayoutInflater inflater;
    private final Context context;
    private final OnClickListener<Parent> listener;

    public ParentsAdapter(Context context, OnClickListener<Parent> listener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_EMPTY:
                return new EmptyViewHolder(inflater.inflate(TYPE_EMPTY, parent, false));
            case TYPE_USER:
                return new UserViewHolder(inflater.inflate(TYPE_USER, parent, false));
            default:
                throw new IllegalArgumentException("Please pass in a valid viewholder instance or subclass");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return parents.isEmpty() ? TYPE_EMPTY : TYPE_USER;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserViewHolder) bindUserViewHolder((UserViewHolder) holder, position);
        else bindEmptyViewHolder((EmptyViewHolder) holder);
    }

    private void bindEmptyViewHolder(EmptyViewHolder holder) {
        // Load GIF file into the image view
        GlideApp.with(context)
                .asGif()
                .load(R.drawable.not_found)
                .placeholder(R.color.content_placeholder)
                .error(R.color.content_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .priority(Priority.IMMEDIATE)
                .into(holder.imageView);
    }

    private void bindUserViewHolder(UserViewHolder holder, int position) {

        Parent parent = parents.get(position);
        holder.username.setText(parent.getName());
        holder.info.setText(parent.getEmail()); // TODO: 005 05.05.19 Get additional information about this parent (Like date added and so on)

        // Load profile image
        GlideApp.with(context)
                .load(parent.getAvatar())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .circleCrop()
                .priority(Priority.IMMEDIATE)
                .placeholder(R.drawable.avatar_placeholder)
                .error(R.drawable.ic_player)
                .transition(withCrossFade())
                .into(holder.avatar);

        holder.itemView.setOnLongClickListener(v -> {
            listener.onClick(parent, true);
            return true;
        });

        holder.itemView.setOnClickListener(v -> listener.onClick(parent, false));
    }

    @Override
    public int getItemCount() {
        return parents.isEmpty() ? 1 : parents.size();
    }

    public void addData(List<Parent> parents) {
        this.parents.clear();
        this.parents.addAll(parents);
        notifyDataSetChanged();
    }
}
