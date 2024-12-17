package com.example.testchatfragment;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LightSensorFragment extends Fragment {

    private TextView lightLevelText;
    private static final int REQUEST_WRITE_SETTINGS_PERMISSION = 1;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_light_sensor, container, false);

        lightLevelText = view.findViewById(R.id.lightLevelText);
        checkAndRequestWriteSettingsPermission();  // Cek izin saat fragment dibuat

        lightLevelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adjustScreenBrightness();  // Sesuaikan kecerahan layar saat light sensor diklik
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cek izin setiap kali fragment menjadi visible kembali
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(requireContext())) {
            adjustScreenBrightness();  // Ubah kecerahan layar jika izin sudah diberikan
        }
    }

    // Pengecekan dan Permintaan Izin
    private void checkAndRequestWriteSettingsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(requireContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
                startActivity(intent);
            } else {
                Log.d("Permission Check", "Write settings permission granted.");
                adjustScreenBrightness();  // Ubah kecerahan layar jika izin telah diberikan
            }
        }
    }

    private void changeScreenBrightness(int brightnessValue) {
        ContentResolver resolver = requireActivity().getContentResolver();
        Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightnessValue);
        Log.d("Screen Brightness", "Brightness set to: " + brightnessValue);
    }

    // Logika Penyesuaian Kecerahan berdasarkan level baterai
    @SuppressLint("SetTextI18n")
    private void adjustScreenBrightness() {
        // Mendapatkan status baterai
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = requireActivity().registerReceiver(null, intentFilter);

        // Mendapatkan level dan status baterai
        if (batteryStatus != null) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int) ((level / (float) scale) * 100);  // Persentase baterai

            Log.d("Battery Status", "Battery Level: " + batteryPct + "%");

            // Mengatur kecerahan layar berdasarkan level baterai
            int brightness;
            if (batteryPct > 70) {
                brightness = 255;  // Kecerahan penuh
            } else if (batteryPct > 30) {
                brightness = 127;  // Kecerahan sedang
            } else {
                brightness = 50;   // Kecerahan rendah
            }

            // Mengatur kecerahan layar
            if (Settings.System.canWrite(requireContext())) {
                changeScreenBrightness(brightness);
                // Menampilkan status baterai dan kecerahan di TextView
                lightLevelText.setText("Battery Level: " + batteryPct + "%\nScreen Brightness: " + brightness);
            } else {
                // Minta izin kepada pengguna untuk menulis pengaturan
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
                startActivity(intent);
            }
        }
    }
}
