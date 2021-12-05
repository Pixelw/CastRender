package tech.pixelw.castrender.entity;

/**
 * @author Carl Su "Pixelw"
 * @date 2021/10/11
 */
public class MediaEntity {
    private String mediaUrl;
    private String iconUrl;
    private String title;
    private String casterName;

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCasterName() {
        return casterName;
    }

    public void setCasterName(String casterName) {
        this.casterName = casterName;
    }
}
