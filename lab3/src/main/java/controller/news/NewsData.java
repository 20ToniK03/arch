package controller.news;

import java.util.ArrayList;
import java.util.List;

/**
 * Входные данные для создания/редактирования новости (тип {@code NewsData}
 * из UML: используется в сигнатурах {@code Journalist.createNews/editNews}).
 */
public class NewsData {

    private String title;
    private String text;
    private String annotation;
    private String region;
    private List<Long> categoryIds = new ArrayList<Long>();
    private List<String> tagNames = new ArrayList<String>();

    public NewsData() {
    }

    public NewsData(String title, String text, String annotation, String region,
                    List<Long> categoryIds, List<String> tagNames) {
        this.title = title;
        this.text = text;
        this.annotation = annotation;
        this.region = region;
        this.categoryIds = categoryIds;
        this.tagNames = tagNames;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public List<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
    }
}
