package io.codelabs.digitutor.view.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.codelabs.digitutor.R;
import io.codelabs.digitutor.core.util.Constants;
import io.codelabs.digitutor.core.util.OnClickListener;
import io.codelabs.digitutor.data.model.Parent;
import io.codelabs.digitutor.data.model.Request;
import io.codelabs.digitutor.view.adapter.viewholder.EmptyViewHolder;
import io.codelabs.digitutor.view.adapter.viewholder.UserViewHolder;
import io.codelabs.sdk.glide.GlideApp;
import io.codelabs.sdk.util.ExtensionUtils;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class RequestsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Request> requests = new ArrayList<>(0);
    private static final int TYPE_EMPTY = R.layout.item_empty;
    private static final int TYPE_USER = R.layout.item_user;

    private final LayoutInflater inflater;
    private final Activity context;
    private final OnClickListener<Request> listener;
    private final FirebaseFirestore firestore;

    public RequestsAdapter(Activity context, OnClickListener<Request> listener, FirebaseFirestore firestore) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
        this.firestore = firestore;
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
        return requests.isEmpty() ? TYPE_EMPTY : TYPE_USER;
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

    private void bindUserViewHolder(@NotNull UserViewHolder holder, int position) {

        Request request = requests.get(position);

        firestore.collection(Constants.PARENTS).document(request.getParent())
                .addSnapshotListener(context, (documentSnapshot, e) -> {
                    if (e != null) {
                        ExtensionUtils.debugLog(context, e.getLocalizedMessage());
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Parent user = documentSnapshot.toObject(Parent.class);

                        holder.username.setText(Objects.requireNonNull(user).getName());
                        holder.info.setText(String.format(Locale.getDefault(), "%d ward(s)", user.getWards().size()));

                        // Load profile image
                        GlideApp.with(context)
                                .load(user.getAvatar())
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .circleCrop()
                                .priority(Priority.IMMEDIATE)
                                .placeholder(R.drawable.avatar_placeholder)
                                .error(R.drawable.ic_player)
                                .transition(withCrossFade())
                                .into(holder.avatar);
                    }
                });

        holder.itemView.setOnLongClickListener(v -> {
            listener.onClick(request, true);
            return true;
        });

        holder.itemView.setOnClickListener(v -> listener.onClick(request, false));
    }

    @Override
    public int getItemCount() {
        return requests.isEmpty() ? 1 : requests.size();
    }

    public void addData(List<Request> requests) {
        this.requests.clear();
        this.requests.addAll(requests);
        notifyDataSetChanged();
    }
}
