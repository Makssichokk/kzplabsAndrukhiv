import java.util.*;
import java.util.function.Predicate;
/** Типобезпечне сховище з незмінними знімками. @param <T> тип елементів */
public final class Repository<T> {
    private final List<T> items = new ArrayList<>();
    /** Додає ненульовий елемент. @param item елемент */
    public void add(T item) { items.add(Objects.requireNonNull(item, "item")); }
    /** Повертає незмінний знімок. @return список */
    public List<T> all() { return List.copyOf(items); }
    /** Знаходить елементи за умовою. @param predicate умова @return незмінний список */
    public List<T> find(Predicate<? super T> predicate) { Objects.requireNonNull(predicate); return items.stream().filter(predicate).toList(); }
    /** Кількість записів. @return кількість */
    public int size() { return items.size(); }
}
