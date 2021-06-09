package com.rakhatali.demal.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rakhatali.demal.EditProfileActivity;
import com.rakhatali.demal.MainActivity;
import com.rakhatali.demal.NavActivity;
import com.rakhatali.demal.R;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class SettingsFragment extends Fragment {

    public static final String[] languages ={"Тілдер/Языки","Орысша/Русский","Қазақша/Казахский"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //view.findViewById(R.id.bottom_navigation).setVisibility(View.GONE);
        getActivity().findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);

        Spinner spinner = view.findViewById(R.id.spinner);
        Button exitBtn = view.findViewById(R.id.exit_btn);
        Button changePass = view.findViewById(R.id.passwordChange);
        Button editProfile = view.findViewById(R.id.editProfile);

        final GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());

        ImageButton backBtn = view.findViewById(R.id.setting_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MoreFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ChangePassFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(signInAccount != null){
                    GoogleSignIn.getClient(getContext(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                            .signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                            Intent intent = new Intent(getActivity(), MainActivity.class);
//                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), R.string.exit_error , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.spinner_item,languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedLang = adapterView.getItemAtPosition(i).toString();
                switch (selectedLang) {
                    case "Орысша/Русский":
                        setLocal(getActivity(), "rus");
                        startActivity(new Intent(getActivity(), NavActivity.class));
                        break;
                    case "Қазақша/Казахский":
                        setLocal(getActivity(), "kk");
                        startActivity(new Intent(getActivity(), NavActivity.class));
                        break;
                    case "English":
                        //setLocal(getActivity(),"en");
//                    Intent intent = new Intent(MainActivity.this, ScreenTwo.class);
//                    startActivity(intent);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    public void setLocal(Activity activity, String langCode){
        Locale locale = new Locale(langCode);
        locale.setDefault(locale);

        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config,resources.getDisplayMetrics());

        SharedPreferences sPref = getActivity().getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("localization", langCode);
        ed.commit();

    }

}