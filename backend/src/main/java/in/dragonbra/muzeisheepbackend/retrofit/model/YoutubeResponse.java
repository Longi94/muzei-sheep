package in.dragonbra.muzeisheepbackend.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import in.dragonbra.muzeisheepbackend.retrofit.model.youtube.PageInfo;
import in.dragonbra.muzeisheepbackend.retrofit.model.youtube.PlaylistItem;

import java.util.List;

public class YoutubeResponse {

    @SerializedName("kind")
    @Expose
    private String kind;

    @SerializedName("etag")
    @Expose
    private String etag;

    @SerializedName("nextPageToken")
    @Expose
    private String nextPageToken;

    @SerializedName("pageInfo")
    @Expose
    private PageInfo pageInfo;

    @SerializedName("items")
    @Expose
    private List<PlaylistItem> items = null;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<PlaylistItem> getItems() {
        return items;
    }

    public void setItems(List<PlaylistItem> items) {
        this.items = items;
    }

}