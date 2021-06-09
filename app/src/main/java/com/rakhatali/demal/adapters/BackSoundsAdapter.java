package com.rakhatali.demal.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rakhatali.demal.models.BackSound;
import com.rakhatali.demal.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Objects;

import static com.rakhatali.demal.NavActivity.home_container;

public class BackSoundsAdapter extends RecyclerView.Adapter<BackSoundsAdapter.ViewHolder> {

    public interface OnMusicClickListener {
        void onMusicClick(int position, String backUrl, File temp);
    }

    private final Context mContext;
    private final List<BackSound> mBack;
    private int change= -1;
    final BackSoundsAdapter.OnMusicClickListener listener;
    private ImageView downloading;
    private File localFile;
    public BackSoundsAdapter(Context mContext, List<BackSound> mBack, BackSoundsAdapter.OnMusicClickListener listener) {
        this.mContext = mContext;
        this.mBack = mBack;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.back_sound_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final BackSound backSound = mBack.get(position);
        final String backUrl = backSound.getMusicUrl();

        File parent = new File(mContext.getApplicationInfo().dataDir, "Demal/BackgroundMusic");
        if (!parent.exists())
            parent.mkdirs();

        localFile = new File(parent, backUrl.substring(backUrl.lastIndexOf("/") + 1));
        if(localFile.exists())
            holder.download.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMusicClick(position, backSound.getMusicUrl(),localFile);

                change = position;
                notifyDataSetChanged();
            }
        });
        if(change == position){
            holder.parent.setBackgroundColor(mContext.getResources().getColor(R.color.lightBlue));
        }
        else
        {
            holder.parent.setBackgroundColor(mContext.getResources().getColor(R.color.backForText));
        }

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //downloadBackSound(temp, ref, holder);

                new DownloadFileFromURL().execute(backUrl);
                downloading = holder.download;

            }
        });

        holder.music_name.setText(backSound.getName());

    }

    @Override
    public int getItemCount() {
        return mBack.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView music_name;
        public final ImageView download;
        public final RelativeLayout parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            download = itemView.findViewById(R.id.download);
            music_name = itemView.findViewById(R.id.music_name);
            parent = itemView.findViewById(R.id.parent);
        }
    }

    public class DownloadFileFromURL extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lenghtOfFile = connection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                OutputStream output = new FileOutputStream(localFile);

                byte[] data = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;

                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    output.write(data, 0, count);
                }

                output.flush();

                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            downloading.setVisibility(View.GONE);
        }
    }

}
