package com.rakhatali.demal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rakhatali.demal.R;
import com.rakhatali.demal.models.Emoji;

import java.util.List;


public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.ViewHolder> {

    public interface OnEmojiClickListener{
        void onEmojiClick(int imageResource);
    }
    private final OnEmojiClickListener onEmojiClickListener;

    private final Context mContext;
    private final List<Emoji> mEmoji;

    public EmojiAdapter(Context mContext, List<Emoji> mEmoji, OnEmojiClickListener onEmojiClickListener) {
        this.mContext = mContext;
        this.mEmoji = mEmoji;
        this.onEmojiClickListener = onEmojiClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.emoji_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Emoji emoji = mEmoji.get(position);

        holder.image.setImageResource(emoji.getEmojiResource());
        holder.name.setText(emoji.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEmojiClickListener.onEmojiClick(emoji.getEmojiResource());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEmoji.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;

        public ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.emoji_name);
            image = itemView.findViewById(R.id.emoji_image);
           

        }
    }

}
