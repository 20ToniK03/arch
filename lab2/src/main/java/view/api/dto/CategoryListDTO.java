package view.api.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO списка категорий.
 */
public class CategoryListDTO {

    private List<CategoryDTO> categories = new ArrayList<CategoryDTO>();
    private int total;

    public CategoryListDTO() {
    }

    public CategoryListDTO(List<CategoryDTO> categories) {
        this.categories = categories;
        this.total = categories == null ? 0 : categories.size();
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDTO> categories) {
        this.categories = categories;
        this.total = categories == null ? 0 : categories.size();
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
