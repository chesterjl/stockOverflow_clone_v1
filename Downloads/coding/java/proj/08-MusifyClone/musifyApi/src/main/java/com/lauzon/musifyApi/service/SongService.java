package com.lauzon.musifyApi.service;

import com.lauzon.musifyApi.dto.request.SongRequest;
import com.lauzon.musifyApi.dto.response.SongResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SongService {

    SongResponse addSong(SongRequest request) throws IOException;

    SongResponse updateSong(SongRequest request, String songId) throws IOException;

    Map<String, String> deleteSong(String songId);

    List<SongResponse> viewAllSongs();

    List<SongResponse> viewSongByName(String name);

    List<SongResponse> searchSongs(String keyword);

    List<SongResponse> viewAllSongsForCurrentArtist();

    List<SongResponse> viewAllSongsInAlbum(String albumId);

}
