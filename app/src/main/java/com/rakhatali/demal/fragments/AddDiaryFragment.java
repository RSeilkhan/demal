package com.rakhatali.demal.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.rakhatali.demal.R;
import com.rakhatali.demal.adapters.DiaryAdapter;
import com.rakhatali.demal.adapters.EmojiAdapter;
import com.rakhatali.demal.models.Diary;
import com.rakhatali.demal.models.Emoji;
import com.rakhatali.demal.utils.TextViewUndoRedo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class AddDiaryFragment extends Fragment {

    private Toolbar mToolbar;
    private BottomAppBar bottomAppBar;
    private EditText titleField,contentField;
    private ProgressDialog mProgress;
    private FloatingActionButton fab;
    private DatabaseReference mRef,impRef;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    //private boolean textChanged=false;
    private String saveCurrentDate=null,saveCurrentTime=null,time=null,key=null;
    private boolean isImp=false;
    private String diaryId;
    private int selectedEmoji;
    ArrayList<Emoji> emojiList = new ArrayList<Emoji>();

    private EmojiAdapter emojiAdapter;
    private RecyclerView recyclerView_diary;
    ImageView emojiImageChecker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_diary, container, false);
        getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.GONE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        TextView day = view.findViewById(R.id.add_diary_day);
        TextView month = view.findViewById(R.id.add_diary_month);
        TextView weekDay = view.findViewById(R.id.add_diary_week_day);
        recyclerView_diary = view.findViewById(R.id.recycler_view_emoji);

        Date date=new Date();

        String dayWeekText = new SimpleDateFormat("EEE").format(date);
        String day_t = new SimpleDateFormat("dd").format(date);
        String month_t = new SimpleDateFormat("MMM").format(date);

        weekDay.setText(dayWeekText);
        day.setText(day_t);
        month.setText(month_t);

        titleField=view.findViewById(R.id.add_notes_title);
        contentField=view.findViewById(R.id.add_notes_note);
        emojiImageChecker = view.findViewById(R.id.emoji_image_checker);

        FloatingActionButton fabs = view.findViewById(R.id.fab);

        mToolbar = view.findViewById(R.id.add_notes_bar);

        setHasOptionsMenu(true);

        bottomAppBar=view.findViewById(R.id.bottomAppBar);
        bottomAppBar.replaceMenu(R.menu.add_down_menu);

        setmToolbar();

        Bundle bundle = this.getArguments();
        String title_t = "", content_t = "";

        if (bundle != null) {
            if (bundle.getString("diaryTitle") != null) {
                title_t= bundle.getString("diaryTitle");
            }else if(bundle.getString("diaryContent") != null){
                content_t = bundle.getString("diaryContent");
            }
        }

        mRef = FirebaseDatabase.getInstance().getReference().child("Diaries");

        if(!title_t.equals("")){
            isImp = true;
            titleField.setText(title_t);
            diaryId = bundle.getString("diaryId");
        }else if(!content_t.equals("")){
            contentField.setText(content_t);
        }
        else{
            diaryId = mRef.push().getKey();
        }

        fabs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyDetails();
            }
        });


        final TextViewUndoRedo nTextViewUndoRedo = new TextViewUndoRedo(titleField);
        final TextViewUndoRedo mTextViewUndoRedo = new TextViewUndoRedo(contentField);

        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.undo){

                    if (titleField.hasFocus()){
                        if (!nTextViewUndoRedo.getCanUndo()){
                        }else{
                            nTextViewUndoRedo.undo();

                        }
                    }

                    if (contentField.hasFocus()){

                        if (!mTextViewUndoRedo.getCanUndo()){
                        }else{
                            mTextViewUndoRedo.undo();

                        }
                    }

                }

                if (item.getItemId()==R.id.redo){
                    if (titleField.hasFocus()){

                        if (!nTextViewUndoRedo.getCanRedo()){
                        }else{
                            nTextViewUndoRedo.redo();
                        }
                    }

                    if (contentField.hasFocus()){
                        if (!mTextViewUndoRedo.getCanRedo()){
                        }else{
                            mTextViewUndoRedo.redo();
                        }
                    }
                }


                return false;
            }
        });


        setEmojiRecycler();

        return view;
    }

    private void setmToolbar(){
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.diary);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new DiaryFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void setEmojiRecycler(){
        setInitialData();
        recyclerView_diary.setHasFixedSize(true);
        recyclerView_diary.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        EmojiAdapter.OnEmojiClickListener emojiClickListener = new EmojiAdapter.OnEmojiClickListener() {
            @Override
            public void onEmojiClick(int imageResource) {
                selectedEmoji = imageResource;
                emojiImageChecker.setImageResource(imageResource);
                System.out.println("selectedEmoji: " + selectedEmoji);
            }
        };

        emojiAdapter = new EmojiAdapter(getContext(), emojiList, emojiClickListener);
        recyclerView_diary.setAdapter(emojiAdapter);
    }

    private void setInitialData(){

        emojiList.add(new Emoji ("Радостный",  R.drawable.ic_emoji_happy));
        emojiList.add(new Emoji ("Злой",  R.drawable.ic_emoji_angry));
        emojiList.add(new Emoji ("Крутой",  R.drawable.ic_emoji_cool));
        emojiList.add(new Emoji ("Плачу",  R.drawable.ic_emoji_crying));
        emojiList.add(new Emoji ("Влюблен",  R.drawable.ic_emoji_in_love));
        emojiList.add(new Emoji ("Больной",  R.drawable.ic_emoji_sick));
        emojiList.add(new Emoji ("Грустный",  R.drawable.ic_emoji_sad));
        emojiList.add(new Emoji ("Удивленный",  R.drawable.ic_emoji_surprised));
        emojiList.add(new Emoji ("Взволнованный",  R.drawable.ic_emoji_sweat));
        emojiList.add(new Emoji ("Смущенный",  R.drawable.ic_emoji_confused));

    }

    private void verifyDetails() {
        String title= titleField.getText().toString().trim();
        String content= contentField.getText().toString().trim();

        if (title.isEmpty() || title.length() <5){
            titleField.setError(getString(R.string.title_length_not_more_4));
            titleField.requestFocus();
            return;
        }

        if (content.isEmpty() || content.length() <4){
            contentField.setError(getString(R.string.content_length_4));
            contentField.requestFocus();
            return;
        }

        if(isImp){
            editDiary(title,content );
        }else{
            saveNewDiary(title,content);
        }

    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_up_menu,menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.up_delete){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.confirm_delete);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    mRef.child(mUser.getUid()).child(diaryId).removeValue();
                    impRef.child(key).removeValue();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new DiaryFragment())
                            .addToBackStack(null)
                            .commit();

                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);

    }

    private void editDiary(String title, String diary){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Diaries").child(mUser.getUid());

        HashMap map =new HashMap<>();
        map.put("title",title);
        map.put("userId",mUser.getUid());
        map.put("content",diary);
        //map.put("addedDate", ServerValue.TIMESTAMP);
        map.put("imageName", selectedEmoji);

        reference.child(diaryId).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    Toast.makeText(getContext(), R.string.diary_updated, Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new DiaryFragment())
                            .addToBackStack(null)
                            .commit();
                }else{
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void saveNewDiary(String title, String diary) {

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Diaries").child(mUser.getUid());

        HashMap map =new HashMap<>();
        map.put("id", diaryId);
        map.put("title", title);
        map.put("userId", mUser.getUid());
        map.put("content", diary);
        map.put("addedDate", ServerValue.TIMESTAMP);
        map.put("imageName", selectedEmoji);

        reference2.child(diaryId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getContext(), R.string.new_note_added , Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new DiaryFragment())
                            .addToBackStack(null)
                            .commit();
                }else{
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}