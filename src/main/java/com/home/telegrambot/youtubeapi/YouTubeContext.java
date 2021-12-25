package com.home.telegrambot.youtubeapi;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Setter
@Slf4j
@Component
public class YouTubeContext {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final long NUMBER_OF_VIDEOS_RETURNED = 5;
    private static final long NUMBER_OF_CHANNELS_RETURNED = 3;

    private static YouTube youTube;
    @Value("${telegrambot.apiKey}")
    private String apiKey;

    public SearchListResponse searchForVideo(String term) throws IOException {
        SearchListResponse searchListResponse = null;

        try {
            youTube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {}).setApplicationName("youtube-cmdline-search-sample").build();

            YouTube.Search.List search = youTube.search().list("id,snippet");

            search.setKey(apiKey);
            search.setQ(term);
            search.setType("video");
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            searchListResponse = search.execute();
        } catch (GoogleJsonResponseException e) {
            log.info(e.getMessage());
        }

        return searchListResponse;
    }

    public SearchListResponse searchForChannel(String keyWords) {
        SearchListResponse searchListResponse = null;

        try {
            youTube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, request -> {}).setApplicationName("youtube-cmdline-search-sample").build();
            YouTube.Search.List search = youTube.search().list("id,snippet");

            search.setKey(apiKey);
            search.setQ(keyWords);
            search.setType("channel");
            search.setFields("items(id/kind,id/channelId, snippet/title, snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_CHANNELS_RETURNED);

            searchListResponse = search.execute();
        } catch (IOException e) {
            log.info(e.getMessage());
        }

        return searchListResponse;
    }
}
