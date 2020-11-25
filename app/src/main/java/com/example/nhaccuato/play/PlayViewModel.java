package com.example.nhaccuato.play;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nhaccuato.Models.Song;
import com.example.nhaccuato.Models.SongRespone;
import com.example.nhaccuato.Utils.Constants;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlayViewModel extends ViewModel {
    private SongService service = new SongService();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<List<Song>> songsMutableLiveData = new MutableLiveData<>();

    public PlayViewModel() {

    }

    public void init() {
        compositeDisposable.add(service.getSongRespone()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SongRespone>() {
                    @Override
                    public void accept(SongRespone songRespone) throws Throwable {
                        songsMutableLiveData.setValue(songRespone.getSongList());
                    }
                })
        );
    }

    public String getFullUrl(String endpoint) {
        return Constants.SONG_BASE_URL + endpoint;
    }


    public LiveData<List<Song>> getSongsLiveData() {
        return songsMutableLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}