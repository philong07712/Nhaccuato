package com.example.nhaccuato.offline;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.nhaccuato.MainActivity;
import com.example.nhaccuato.R;
import com.example.nhaccuato.Utils.ConvertHelper;
import com.example.nhaccuato.Utils.SaveHelper;
import com.example.nhaccuato.models.Song;
import com.example.nhaccuato.offline.OfflineSongAdapter;
import com.example.nhaccuato.offline.OfflineViewModel;
import com.example.nhaccuato.offline.PlayableItemListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OfflineFragment extends Fragment {

    private static final int READ_STORAGE_REQUEST_CODE = 111;
    private static final String TAG = OfflineFragment.class.getSimpleName();
    private static final int WRITE_STORAGE_REQUEST_CODE = 112;
    private OfflineViewModel mViewModel;
    private List<Song> songList;
    private RecyclerView recyclerView;
    private boolean isDupClick = false;
    PlayableItemListener playableItemListener = new PlayableItemListener() {
        @Override
        public void onClick(List<Song> songs, int position) {
            ((MainActivity) getActivity()).mPassData.onChange(songs, position);
        }
    };
    private ProgressBar progressBarLoad;

    public static OfflineFragment newInstance() {
        return new OfflineFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offline, container, false);
        recyclerView = view.findViewById(R.id.rv_offline);
        progressBarLoad = view.findViewById(R.id.pb_load_offline);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(OfflineViewModel.class);
        if (checkPermissionForReadExternalStorage()) {
            loadAudio();
        }
        else {
            requestPermissionForReadExternalStorage();
        }

        if (!checkPermissionForWriteExternalStorage()) requestPermissionForWriteExternalStorage();
        // TODO: Use the ViewModel
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void loadAudio() {
        preLoadingView();
        // create new thread to run the fetch data offline
        songList = new ArrayList<>();
        ContentResolver contentResolver = getActivity().getContentResolver();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Map<String, Song> prevMap = SaveHelper.loadOfflineSong(getContext());
                boolean isDataChanged = false;
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String selector = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
                String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
                Cursor cursor = contentResolver.query(uri, null, selector, null, sortOrder);

                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        Song currentSong = new Song();
                        String idSong = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

                        if (!prevMap.containsKey(idSong)) {
                            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                            String idArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                            String artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                            // this will retrieve thumbnail Uri by song data
                            String thumbnail = retrieveThumbnailSong(data, idSong);
                            currentSong = new Song(data, title, idSong, idArtist, artistName, thumbnail);
                            // This will identify if offline data has changed
                            prevMap.put(idSong, currentSong);
                            isDataChanged = true;
                            Log.d(TAG, "run: not contains");
                        }

                        else {
                            currentSong = prevMap.get(idSong);
                            Log.d(TAG, "run: contains");
                        }

                        songList.add(currentSong);
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateLoadingView();
                    }
                });
                cursor.close();
                // if the data set has been changed, case new song added, we will save map again
                if (isDataChanged) {
                    SaveHelper.saveOfflineSong(getContext(), prevMap);
                }
                Log.d(TAG, "run: allDone ");
            }
        };

        new Thread(runnable).start();
    }

    private String retrieveThumbnailSong(String data, String title) {
        String path = null;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Bitmap thumbnailBitmap = null;
        BitmapFactory.Options bfo=new BitmapFactory.Options();

        mmr.setDataSource(getContext(), Uri.parse(data));
        rawArt = mmr.getEmbeddedPicture();

        // if rawArt is null then no cover art is embedded in the file or is not
        // recognized as such.
        if (null != rawArt) {
            thumbnailBitmap = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
        }

        // Convert that bitmap to uri
        if (thumbnailBitmap != null) {
            Uri uri = ConvertHelper.getImageUri(getActivity(), thumbnailBitmap, title);
            if (uri != null) path = uri.toString();
        }
        return path;
    }

    private void preLoadingView() {
        progressBarLoad.setVisibility(View.VISIBLE);
    }

    private void updateLoadingView() {
        progressBarLoad.setVisibility(View.INVISIBLE);
        // pass data to main
        if (((MainActivity) getActivity()).passDataOffline != null) {
            ((MainActivity) getActivity()).passDataOffline.onChange(songList, 0);
        }
        setupRecylerView();
    }

    private void setupRecylerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        OfflineSongAdapter offlineSongAdapter = new OfflineSongAdapter(songList, playableItemListener);
        recyclerView.setAdapter(offlineSongAdapter);
        recyclerView.setLayoutManager(layoutManager);
        // this will indicate the onClick for the song
    }

    private boolean checkPermissionForReadExternalStorage() {
        // this will check if user granted permission for the app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getActivity().getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    private void requestPermissionForReadExternalStorage() {
        // request user to give permission to app
        try {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkPermissionForWriteExternalStorage() {
        // this will check if user granted permission for the app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getActivity().getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    private void requestPermissionForWriteExternalStorage() {
        // request user to give permission to app
        try {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_STORAGE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}