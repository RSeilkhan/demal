package com.rakhatali.demal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.rakhatali.demal.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DiaryDetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary_detail, container, false);

        TextView title = view.findViewById(R.id.diary_title);
        TextView content = view.findViewById(R.id.diary_content);
        final TextView day = view.findViewById(R.id.diary_day);
        TextView weekDay = view.findViewById(R.id.diary_week_day);
        TextView month = view.findViewById(R.id.diary_month);
        TextView year = view.findViewById(R.id.diary_year);

        Bundle bundle = this.getArguments();
        final String title_t = bundle.getString("diaryTitle");
        final String content_t = bundle.getString("diaryContent");
        final String id = bundle.getString("diaryId");

        long date = bundle.getLong("diaryDate");
        Date netDate = (new Date(date));

        String dayWeekText = new SimpleDateFormat("EEE").format(netDate);
        String day_t = new SimpleDateFormat("dd").format(netDate);
        String month_t = new SimpleDateFormat("MMMM").format(netDate);
        String year_t = new SimpleDateFormat("yyyy").format(netDate);

        title.setText(title_t);
        content.setText(content_t);
        day.setText(dayWeekText);
        weekDay.setText(day_t);
        month.setText(month_t);
        year.setText(year_t);

        final ImageButton editDiary = view.findViewById(R.id.edit_diary);

        editDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDiary(title_t, content_t, id);
            }
        });

        //https://guides.codepath.com/android/using-the-recyclerview#animators top site

        return view;
    }

    private void editDiary(String title, String content, String id){
        Bundle bundle = new Bundle();
        bundle.putString("diaryTitle", title);
        bundle.putString("diaryContent", content);
        bundle.putString("diaryId", id);

        AddDiaryFragment addDiaryFragment = new AddDiaryFragment();
        addDiaryFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,  addDiaryFragment).commit();
    }

}