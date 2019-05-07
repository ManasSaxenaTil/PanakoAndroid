package com.example.mxaudiorecogition;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Track {

    @SerializedName("atw")
    private String atw;

    @SerializedName("seokey")
    private String seokey;

    @SerializedName("albumseokey")
    private String albumseokey;

    @SerializedName("track_id")
    private String trackId;

    @SerializedName("track_title")
    private String trackTitle;

    @SerializedName("album_title")
    private String albumTitle;

    @SerializedName("language")
    private String language;

    @SerializedName("artwork")
    private String artwork;

    @SerializedName("artwork_web")
    private String artworkWeb;

    @SerializedName("artwork_large")
    private String artworkLarge;

    @SerializedName("artist")
    private List<Artist> artists;

    @SerializedName("gener")
    private List<Genre> geners;

    @SerializedName("youtube_id")
    private String youtubeId;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("isrc")
    private String isrcCode;

    @SerializedName("parental_warning")
    private int parentalWarning;

    @SerializedName("popularity")
    private String popularity;

    public Track() {
        super();
    }

    public String getAtw() {
        return atw;
    }

    public void setAtw(String atw) {
        this.atw = atw;
    }

    public String getSeokey() {
        return seokey;
    }

    public void setSeokey(String seokey) {
        this.seokey = seokey;
    }

    public String getAlbumseokey() {
        return albumseokey;
    }

    public void setAlbumseokey(String albumseokey) {
        this.albumseokey = albumseokey;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getArtwork() {
        return artwork;
    }

    public void setArtwork(String artwork) {
        this.artwork = artwork;
    }

    public String getArtworkWeb() {
        return artworkWeb;
    }

    public void setArtworkWeb(String artworkWeb) {
        this.artworkWeb = artworkWeb;
    }

    public String getArtworkLarge() {
        return artworkLarge;
    }

    public void setArtworkLarge(String artworkLarge) {
        this.artworkLarge = artworkLarge;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public List<Genre> getGeners() {
        return geners;
    }

    public void setGeners(List<Genre> geners) {
        this.geners = geners;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getIsrcCode() {
        return isrcCode;
    }

    public void setIsrcCode(String isrcCode) {
        this.isrcCode = isrcCode;
    }

    public int getParentalWarning() {
        return parentalWarning;
    }

    public void setParentalWarning(int parentalWarning) {
        this.parentalWarning = parentalWarning;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((albumTitle == null) ? 0 : albumTitle.hashCode());
        result = prime * result + ((albumseokey == null) ? 0 : albumseokey.hashCode());
        result = prime * result + ((artists == null) ? 0 : artists.hashCode());
        result = prime * result + ((artwork == null) ? 0 : artwork.hashCode());
        result = prime * result + ((artworkLarge == null) ? 0 : artworkLarge.hashCode());
        result = prime * result + ((artworkWeb == null) ? 0 : artworkWeb.hashCode());
        result = prime * result + ((atw == null) ? 0 : atw.hashCode());
        result = prime * result + ((geners == null) ? 0 : geners.hashCode());
        result = prime * result + ((isrcCode == null) ? 0 : isrcCode.hashCode());
        result = prime * result + ((language == null) ? 0 : language.hashCode());
        result = prime * result + parentalWarning;
        result = prime * result + ((popularity == null) ? 0 : popularity.hashCode());
        result = prime * result + ((releaseDate == null) ? 0 : releaseDate.hashCode());
        result = prime * result + ((seokey == null) ? 0 : seokey.hashCode());
        result = prime * result + ((trackId == null) ? 0 : trackId.hashCode());
        result = prime * result + ((trackTitle == null) ? 0 : trackTitle.hashCode());
        result = prime * result + ((youtubeId == null) ? 0 : youtubeId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Track other = (Track) obj;
        if (albumTitle == null) {
            if (other.albumTitle != null)
                return false;
        } else if (!albumTitle.equals(other.albumTitle))
            return false;
        if (albumseokey == null) {
            if (other.albumseokey != null)
                return false;
        } else if (!albumseokey.equals(other.albumseokey))
            return false;
        if (artists == null) {
            if (other.artists != null)
                return false;
        } else if (!artists.equals(other.artists))
            return false;
        if (artwork == null) {
            if (other.artwork != null)
                return false;
        } else if (!artwork.equals(other.artwork))
            return false;
        if (artworkLarge == null) {
            if (other.artworkLarge != null)
                return false;
        } else if (!artworkLarge.equals(other.artworkLarge))
            return false;
        if (artworkWeb == null) {
            if (other.artworkWeb != null)
                return false;
        } else if (!artworkWeb.equals(other.artworkWeb))
            return false;
        if (atw == null) {
            if (other.atw != null)
                return false;
        } else if (!atw.equals(other.atw))
            return false;
        if (geners == null) {
            if (other.geners != null)
                return false;
        } else if (!geners.equals(other.geners))
            return false;
        if (isrcCode == null) {
            if (other.isrcCode != null)
                return false;
        } else if (!isrcCode.equals(other.isrcCode))
            return false;
        if (language == null) {
            if (other.language != null)
                return false;
        } else if (!language.equals(other.language))
            return false;
        if (parentalWarning != other.parentalWarning)
            return false;
        if (popularity == null) {
            if (other.popularity != null)
                return false;
        } else if (!popularity.equals(other.popularity))
            return false;
        if (releaseDate == null) {
            if (other.releaseDate != null)
                return false;
        } else if (!releaseDate.equals(other.releaseDate))
            return false;
        if (seokey == null) {
            if (other.seokey != null)
                return false;
        } else if (!seokey.equals(other.seokey))
            return false;
        if (trackId == null) {
            if (other.trackId != null)
                return false;
        } else if (!trackId.equals(other.trackId))
            return false;
        if (trackTitle == null) {
            if (other.trackTitle != null)
                return false;
        } else if (!trackTitle.equals(other.trackTitle))
            return false;
        if (youtubeId == null) {
            if (other.youtubeId != null)
                return false;
        } else if (!youtubeId.equals(other.youtubeId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Track [atw=");
        builder.append(atw);
        builder.append(", seokey=");
        builder.append(seokey);
        builder.append(", albumseokey=");
        builder.append(albumseokey);
        builder.append(", trackId=");
        builder.append(trackId);
        builder.append(", trackTitle=");
        builder.append(trackTitle);
        builder.append(", albumTitle=");
        builder.append(albumTitle);
        builder.append(", language=");
        builder.append(language);
        builder.append(", artwork=");
        builder.append(artwork);
        builder.append(", artworkWeb=");
        builder.append(artworkWeb);
        builder.append(", artworkLarge=");
        builder.append(artworkLarge);
        builder.append(", artists=");
        builder.append(artists);
        builder.append(", geners=");
        builder.append(geners);
        builder.append(", youtubeId=");
        builder.append(youtubeId);
        builder.append(", releaseDate=");
        builder.append(releaseDate);
        builder.append(", isrcCode=");
        builder.append(isrcCode);
        builder.append(", parentalWarning=");
        builder.append(parentalWarning);
        builder.append(", popularity=");
        builder.append(popularity);
        builder.append("]");
        return builder.toString();
    }
}
