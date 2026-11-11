package view.api.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO результата списка новостей: сами элементы плюс их общее количество.
 *
 * <p>Обёртка нужна, чтобы клиент получал не «голый» массив, а именованную структуру,
 * в которую позже можно добавить постраничную навигацию.</p>
 */
public class NewsListDTO {

    private List<NewsTitleDTO> items = new ArrayList<NewsTitleDTO>();
    private int total;

    public NewsListDTO() {
    }

    public NewsListDTO(List<NewsTitleDTO> items) {
        this.items = items;
        this.total = items == null ? 0 : items.size();
    }

    public List<NewsTitleDTO> getItems() {
        return items;
    }

    public void setItems(List<NewsTitleDTO> items) {
        this.items = items;
        this.total = items == null ? 0 : items.size();
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
