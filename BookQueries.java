import java.util.*;
import java.util.stream.Collectors;
/** Незмінні запити Stream API над поліморфним каталогом книжок. */
public final class BookQueries {
    private BookQueries() {}
    /** Вибирає книги, видані від 2020 року; записи без року пропускаються. */
    public static List<Book> recent(List<Book> books) {return books.stream().filter(b->b.getYear()!=null && b.getYear()>=2020).toList();}
    /** Повертає назви всіх книжок, включно з повторами. */
    public static List<String> titles(List<Book> books) {return books.stream().map(Book::getTitle).toList();}
    /** Групує книги за жанром; для старих записів використовує «Не вказано». */
    public static Map<String,Long> countByGenre(List<Book> books) {return books.stream().collect(Collectors.groupingBy(b->b.getGenre()==null?"Не вказано":b.getGenre(),TreeMap::new,Collectors.counting()));}
    /** Обчислює статистику кількості сторінок. */
    public static IntSummaryStatistics pageStatistics(List<Book> books) {return books.stream().collect(Collectors.summarizingInt(Book::getPages));}
    /** Повертає перші N за спаданням сторінок і зростанням назви. */
    public static List<Book> top(List<Book> books,int n) {if(n<0)throw new IllegalArgumentException("N має бути невід’ємним.");return books.stream().sorted(Comparator.comparingInt(Book::getPages).reversed().thenComparing(Book::getTitle)).limit(n).toList();}
    /** Шукає перший збіг за назвою; null дає Optional.empty(). */
    public static Optional<Book> findByTitle(List<Book> books,String title) {if(title==null)return Optional.empty();String key=title.trim();return books.stream().filter(b->b.getTitle().equals(key)).findFirst();}
}
