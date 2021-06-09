package com.rakhatali.demal.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rakhatali.demal.R;
import com.rakhatali.demal.fragments.AddDiaryFragment;
import com.rakhatali.demal.fragments.AudioFragment;
import com.rakhatali.demal.fragments.DiaryDetailFragment;
import com.rakhatali.demal.models.AudioFile;
import com.rakhatali.demal.models.Diary;

import java.io.File;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.rakhatali.demal.NavActivity.audioFiles;
import static com.rakhatali.demal.NavActivity.bottomSheetBehavior;


public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {

    public interface OnDiaryClickListener{
        void onDiaryClick(Diary diary, int position);
    }
    private final OnDiaryClickListener onDiaryClickListener;

    private final Context mContext;
    private final List<Diary> mDiary;

    public DiaryAdapter(Context mContext, List<Diary> mDiary, OnDiaryClickListener onDiaryClickListener) {
        this.mContext = mContext;
        this.mDiary = mDiary;
        this.onDiaryClickListener = onDiaryClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.diary_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Diary diary = mDiary.get(position);
        long date = diary.getAddedDate();
        Date netDate = (new Date(date));

        String dayWeekText = new SimpleDateFormat("EEE").format(netDate);
        String day_t = new SimpleDateFormat("dd").format(netDate);
        String month_t = new SimpleDateFormat("MMMM").format(netDate);

        holder.tittle.setText(diary.getTitle());
        holder.content.setText(diary.getContent());
        holder.weekDay.setText(dayWeekText);
        holder.day.setText(day_t);
        holder.month.setText(month_t);


        holder.diaryImage.setImageResource(diary.getImageName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDiaryClickListener.onDiaryClick(diary, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDiary.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tittle;
        public TextView content;
        public TextView day;
        public TextView month;
        public TextView weekDay;
        public ImageView diaryImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tittle = itemView.findViewById(R.id.tittle);
            content = itemView.findViewById(R.id.content);
            day = itemView.findViewById(R.id.day);
            month = itemView.findViewById(R.id.month);
            weekDay = itemView.findViewById(R.id.day_week);
            diaryImage = itemView.findViewById(R.id.image_diary);

        }
    }

}
