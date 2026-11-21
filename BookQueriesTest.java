import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
/** Перевірки потокових запитів і сумісності старого формату. */
class BookQueriesTest {
    private final List<Book> books=List.of(new PrintedBook("Б", "Автор",200,100,2020,"Роман"),new ElectronicBook("А","Автор",200,80,2022,"Поезія"),new PrintedBook("В","Автор",100,60,2019,"Роман"));
    @Test void filterAndMap(){assertEquals(2,BookQueries.recent(books).size());assertEquals(List.of("Б","А","В"),BookQueries.titles(books));}
    @Test void groupingAndStatistics(){assertEquals(2,BookQueries.countByGenre(books).get("Роман"));assertEquals(500,BookQueries.pageStatistics(books).getSum());assertEquals(3,BookQueries.pageStatistics(books).getCount());}
    @Test void topAndOptional(){assertEquals(List.of("А","Б"),BookQueries.titles(BookQueries.top(books,2)));assertEquals("Б",BookQueries.findByTitle(books," Б ").orElseThrow().getTitle());assertTrue(BookQueries.findByTitle(books,null).isEmpty());assertTrue(BookQueries.findByTitle(books,"Немає").isEmpty());}
    @Test void emptyAndLimits(){assertTrue(BookQueries.top(books,0).isEmpty());assertEquals(3,BookQueries.top(books,20).size());assertThrows(IllegalArgumentException.class,()->BookQueries.top(books,-1));assertEquals(0,BookQueries.pageStatistics(List.of()).getCount());assertTrue(BookQueries.countByGenre(List.of()).isEmpty());}
    @Test void legacyAndImmutability(){assertNull(Book.fromCsv("Стара;Автор;100;20").getYear());assertEquals("Не вказано",BookQueries.countByGenre(List.of(Book.fromCsv("Стара;Автор;100;20"))).keySet().iterator().next());}
}
