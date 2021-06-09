package com.rakhatali.demal.adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.rakhatali.demal.NavActivity;
import com.rakhatali.demal.R;
import com.rakhatali.demal.models.Background;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.rakhatali.demal.NavActivity.home_container;

public class BackgroundAdapter extends RecyclerView.Adapter<BackgroundAdapter.ViewHolder> {


    private final Context mContext;
    private final List<Background> mBack;
    ProgressBar progress_circular;
    public File localFile;
    public BackgroundAdapter(Context mContext, List<Background> mBack) {
        this.mContext = mContext;
        this.mBack = mBack;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.background_item, parent, false);
        return new BackgroundAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Background background = mBack.get(position);

//        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
//        String backLink = background.getImageUrl();
//
//        backLink =  backLink.substring(backLink.indexOf("BackgroundImages"));
//        storageRef.child(backLink).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                progress_circular.setVisibility(View.GONE);
//                Glide.with(mContext).load(uri).transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(40)))
//                        .into(holder.back_image);
//            }
//        });

        Glide.with(mContext).load(background.getImageUrl()).transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(40)))
                        .into(holder.back_image);

        holder.back_name.setText(background.getNameBack());

        holder.back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                downloadBack(background.getImageUrl());

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                downloadBack(background.getImageUrl());
                Intent intent = new Intent(mContext, NavActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBack.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView back_name;
        public final ImageView back_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            progress_circular = itemView.findViewById(R.id.progress_circular);

            back_name = itemView.findViewById(R.id.back_name);
            back_image = itemView.findViewById(R.id.back_image);
        }
    }


    private void downloadBack(String backUrl){
        File dir = new File(mContext.getApplicationInfo().dataDir, "Demal/BackgroundImage");
        if(!dir.exists())
            dir.mkdirs();
        localFile = new File(dir, "BackgroundImage.jpg");

        new DownloadsImage().execute(backUrl);
//        ref.getFile(temp).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Toast.makeText(mContext, "Скачивание не удалось", Toast.LENGTH_SHORT).show();
//            }
//        });
    }


    class DownloadsImage extends AsyncTask<String, Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(strings[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap bm = null;
            try {
                bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream out = null;

            try {
                out = new FileOutputStream(localFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try{
                bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
                out.flush();
                out.close();
                // Tell the media scanner about the new file so that it is
                // immediately available to the user.
                MediaScannerConnection.scanFile(mContext,new String[] { localFile.getAbsolutePath() }, null,new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
            } catch(Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            File dir = new File(mContext.getApplicationInfo().dataDir, "Demal/BackgroundImage");
            final File temp = new File(dir, "BackgroundImage.jpg");
            if (temp.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(temp.getAbsolutePath());
                Bitmap blurredBitmap = NavActivity.blur( mContext, myBitmap );
                home_container.setImageBitmap(blurredBitmap);
            }else{
                Glide.with(mContext).load(R.mipmap.island_back).into(home_container);
            }
//            Intent intent = new Intent(mContext, NavActivity.class);
//            mContext.startActivity(intent);
        }
    }
}



