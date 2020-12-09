package com.example.nhaccuato.offline;


import com.example.nhaccuato.models.Song;

import java.util.List;

public interface PlayableItemListener {
    public void onClick(List<Song> songs, int position);
}
