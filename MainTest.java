import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    @Test
    void reportForEmptyCatalogHasNoData() {
        String report = Main.createReport(List.of(), List.of());
        assertTrue(report.contains("Найдорожча книжка: немає даних"));
        assertTrue(report.contains("Середня кількість сторінок: 0.00"));
    }

    @Test
    void reportCalculatesStatistics() {
        List<Book> books = List.of(
                new Book("А", "Автор", 100, 10),
                new Book("Б", "Автор", 300, 30));
        String report = Main.createReport(books, List.of());
        assertTrue(report.contains("Середня кількість сторінок: 200.00"));
        assertTrue(report.contains("Найдорожча книжка: Б"));
        assertTrue(report.contains("Сумарна вартість каталогу: 40.00 грн"));
    }

    @Test
    void reportListsErrors() {
        String report = Main.createReport(List.of(), List.of("Рядок 2: Порожній рядок."));
        assertTrue(report.contains("Рядок 2: Порожній рядок."));
        assertFalse(report.contains("Помилок не виявлено."));
    }
}
