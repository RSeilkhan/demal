package com.rakhatali.demal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rakhatali.demal.models.Category;
import com.rakhatali.demal.R;

import java.util.List;



public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

    public interface OnTextClickListener {
        void onTextClick(String data);
    }

    private int row_index = -1;
    private final Context mContext;
    private final List<Category> mCategory;
    final OnTextClickListener listener;

    public CategoryAdapter(Context mContext, List<Category> mCategory, OnTextClickListener listener) {
        this.mContext = mContext;
        this.mCategory = mCategory;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.category_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Category category = mCategory.get(position);

        holder.category_name.setText(category.getName());
        holder.category_name.setText(category.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onTextClick(category.getId());
                row_index=position;
            }
        });
        if(row_index==position){
            holder.card_view.setBackgroundColor(mContext.getResources().getColor(R.color.darkBlue));
        }
        else
        {
            holder.card_view.setBackgroundColor(mContext.getResources().getColor(R.color.blue));

        }
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView category_name;
        public final CardView card_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card_view = itemView.findViewById(R.id.card_view);
            category_name = itemView.findViewById(R.id.name);
        }


    }

}
