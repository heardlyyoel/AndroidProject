package com.example.testchatfragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;  // Pastikan menggunakan ImageButton
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;

public class StoriesFragment extends Fragment {
    private VideoView videoView;
    private ImageButton buttonPlayPause;
    private ImageView imageProfile;

    public StoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stories, container, false);

        // Initialize VideoView, ImageButton, and ImageView
        videoView = view.findViewById(R.id.videoPosting);
        buttonPlayPause = view.findViewById(R.id.buttonPlayPause);
        Button buttonCamera = view.findViewById(R.id.buttonCamera); // Ubah menjadi ImageButton
        imageProfile = view.findViewById(R.id.imageProfile);
        ImageButton buttonPlayMusic = view.findViewById(R.id.buttonPlayMusic); // Play music button
        TextView textRelaxMessage = view.findViewById(R.id.textRelaxMessage); // TextView for blinking message

        // Set blinking effect on text
        final Handler handler = new Handler();
        Runnable blinkRunnable = new Runnable() {
            @Override
            public void run() {
                // Toggle visibility between VISIBLE and GONE for blinking effect
                if (textRelaxMessage.getVisibility() == View.VISIBLE) {
                    textRelaxMessage.setVisibility(View.INVISIBLE);
                } else {
                    textRelaxMessage.setVisibility(View.VISIBLE);
                }
                handler.postDelayed(this, 1500); // Set delay
            }
        };
        handler.post(blinkRunnable); // Start blinking

        // Camera button logic
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open PostStoryActivity
                Intent intent = new Intent(getActivity(), PostStoryActivity.class);
                startActivity(intent);
            }
        });

        // Set video from local file
        Uri videoUri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.spngb);
        videoView.setVideoURI(videoUri);

        // Handle video preparation safely
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                try {
                    // Ensure video starts playing
                    videoView.start();
                    buttonPlayPause.setImageResource(R.drawable.pause);
                } catch (Exception e) {
                    Log.e("StoriesFragment", "Error starting video: " + e.getMessage());
                    Toast.makeText(getActivity(), "Error playing video", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle errors during video playback
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e("StoriesFragment", "Error during video playback");
                Toast.makeText(getActivity(), "Error during video playback", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // Listener for play/pause button
        buttonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("StoriesFragment", "Button clicked");
                if (videoView.isPlaying()) {
                    videoView.pause();
                    buttonPlayPause.setImageResource(R.drawable.play);
                    Log.d("StoriesFragment", "Video paused");
                } else {
                    videoView.start();
                    buttonPlayPause.setImageResource(R.drawable.pause);
                    Log.d("StoriesFragment", "Video started");
                }
            }
        });

        // Set onClickListener for imageProfile to display saved image
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySavedImage();
            }
        });

        // Set click listener untuk tombol music
        buttonPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pindah ke MainActivity dan muat MusicFragment
                if (getActivity() != null) {
                    // Memastikan aktivitas yang dipanggil adalah MainActivity
                    MainActivity mainActivity = (MainActivity) getActivity();
                    if (mainActivity != null) {
                        FragmentManager fm = mainActivity.getSupportFragmentManager();
                        fm.beginTransaction()
                                .replace(R.id.fragmentContainerView, new MusicFragment())
                                .setReorderingAllowed(true)
                                .addToBackStack("music")
                                .commit();
                    }
                }
            }
        });

        return view;
    }

    private void displaySavedImage() {
        // Retrieve image path from SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String imagePath = prefs.getString("last_saved_image_path", null); // Find key with last saved image path

        if (imagePath != null) {
            File imgFile = new File(imagePath); // Create object, ensure picture exists in device
            if (imgFile.exists()) { // Check if file exists
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath()); // Convert file image to Bitmap
                imageProfile.setImageBitmap(bitmap); // Set Bitmap to image view
            } else {
                Toast.makeText(getActivity(), "Image file not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "No image saved", Toast.LENGTH_SHORT).show();
        }
    }
}
