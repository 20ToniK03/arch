package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Простейшее хранилище «в памяти» (in-memory store), имитирующее базу данных.
 *
 * <p>Класс относится к модулю модели и ничего не знает о бизнес-объектах и DTO:
 * он параметризован типом {@code T}. Благодаря этому модель не зависит от
 * контроллера и представления.</p>
 *
 * <p>Все операции синхронизированы, так как HTTP-запросы могут обрабатываться
 * параллельно.</p>
 *
 * @param <T> тип хранимой сущности
 */
public class InMemoryStore<T> {

    private final Map<Long, T> data = new LinkedHashMap<Long, T>();

    /** Сохраняет элемент по ключу (перезаписывает существующий). */
    public synchronized void put(Long id, T item) {
        data.put(id, item);
    }

    /** Возвращает элемент по ключу или {@code null}, если его нет. */
    public synchronized T find(Long id) {
        return data.get(id);
    }

    /** Проверяет наличие элемента с указанным ключом. */
    public synchronized boolean contains(Long id) {
        return data.containsKey(id);
    }

    /** Возвращает копию всех элементов в порядке добавления. */
    public synchronized List<T> findAll() {
        return new ArrayList<T>(data.values());
    }

    /** Удаляет элемент по ключу. Возвращает {@code true}, если элемент был. */
    public synchronized boolean remove(Long id) {
        return data.remove(id) != null;
    }

    /** Количество элементов. */
    public synchronized int size() {
        return data.size();
    }

    /** Удаляет все элементы. */
    public synchronized void clear() {
        data.clear();
    }
}
