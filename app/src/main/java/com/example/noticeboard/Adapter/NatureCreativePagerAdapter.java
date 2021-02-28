package com.example.noticeboard.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.noticeboard.Model.Post_Information;
import com.example.noticeboard.R;
import com.squareup.picasso.Picasso;
import com.tbuonomo.creativeviewpager.adapter.CreativePagerAdapter;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;


public final class NatureCreativePagerAdapter implements CreativePagerAdapter {
    @NotNull
    private final Context context;
    ArrayList<Post_Information> all_post=new ArrayList<Post_Information>();
    MediaPlayer mediaPlayer;
    public NatureCreativePagerAdapter(Context context, ArrayList<Post_Information> all_post) {
        super();
        this.all_post=all_post;
        this.context = context;
    }

    @Override
    public View instantiateHeaderItem(@NotNull LayoutInflater inflater, @NotNull ViewGroup container, int position) {
        View headerRoot = inflater.inflate(R.layout.item_creative_content_nature, container, false);
        TextView title = (TextView)headerRoot.findViewById(R.id.all_post_text_view);
        ImageView image = (ImageView)headerRoot.findViewById(R.id.all_post_image);
        TextView user_name=(TextView) headerRoot.findViewById(R.id.user_name);
        if(all_post.get(position).getPost_audio().toString()!= "null"){
            ViewStub  stub = (ViewStub) headerRoot.findViewById(R.id.all_post_audio_layout_stub);
            stub.setLayoutResource(R.layout.music_player_view);
            View inflatedView = stub.inflate();
            ToggleButton play_pause= (ToggleButton) inflatedView.findViewById(R.id.toggleButton);
            play_pause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                         mediaPlayer = new MediaPlayer();
                        try {
                            System.out.println("===================================="+all_post.get(position).getPost_audio().toString());
                            mediaPlayer.setDataSource(context, Uri.parse(all_post.get(position).getPost_audio().toString()));
                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mediaPlayer.start();
                                }
                            });
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        try {
                            mediaPlayer.reset();
                            mediaPlayer.prepare();
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer=null;
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }


        title.setText(all_post.get(position).getPost_edit_text());
        Picasso.get().load(all_post.get(position).getPost_image()).into(image);
        user_name.setText(all_post.get(position).getPost_username());
        return headerRoot;
    }

    @Override
    public View instantiateContentItem(@NotNull LayoutInflater inflater, @NotNull ViewGroup container, int position) {
//        Intrinsics.checkParameterIsNotNull(inflater,"inflater");
//        Intrinsics.checkParameterIsNotNull(container,"container");
        View contentRoot = inflater.inflate(R.layout.item_creative_header_profile, container, false);
        ImageView imageView = (ImageView)contentRoot.findViewById(R.id.itemCreativeImage);
        Picasso.get().load(all_post.get(position).getProfile_pic()).into(imageView);
        return contentRoot;
    }

    public int getCount() { return all_post.size();    }

    public boolean isUpdatingBackgroundColor() {
        return true;
    }


    public NatureCreativePagerAdapter(@NotNull Context context) {
        this.context = context;
    }

    @Override
    public Bitmap requestBitmapAtPosition(int i) {
        return null;
    }
}
