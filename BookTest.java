import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {
    @Test
    void constructorAcceptsValidBook() {
        Book book = new Book("Кобзар", "Тарас Шевченко", 320, 250.50);
        assertEquals("Кобзар", book.getTitle());
        assertEquals("Тарас Шевченко", book.getAuthor());
        assertEquals(320, book.getPages());
        assertEquals(250.50, book.getPrice(), 0.0001);
    }

    @Test
    void constructorRejectsEmptyTitleAndAuthor() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book(" ", "Автор", 100, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new Book("Назва", "", 100, 10));
    }

    @Test
    void constructorRejectsInvalidPageCountAndPrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book("Назва", "Автор", 0, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new Book("Назва", "Автор", 10, -1));
        assertThrows(IllegalArgumentException.class,
                () -> new Book("Назва", "Автор", 10, Double.NaN));
    }

    @Test
    void fromCsvParsesValidLine() {
        Book book = Book.fromCsv("Кобзар;Тарас Шевченко;320;250.50");
        assertEquals("Кобзар", book.getTitle());
        assertEquals(320, book.getPages());
        assertEquals(250.50, book.getPrice(), 0.0001);
    }

    @Test
    void fromCsvRejectsWrongFieldCountAndEmptyFields() {
        assertThrows(IllegalArgumentException.class,
                () -> Book.fromCsv("Неправильний запис"));
        assertThrows(IllegalArgumentException.class,
                () -> Book.fromCsv(";Автор;100;10"));
        assertThrows(IllegalArgumentException.class,
                () -> Book.fromCsv("Назва;;100;10"));
    }

    @Test
    void fromCsvRejectsInvalidNumbers() {
        assertThrows(IllegalArgumentException.class,
                () -> Book.fromCsv("Назва;Автор;abc;10"));
        assertThrows(IllegalArgumentException.class,
                () -> Book.fromCsv("Назва;Автор;100;-10"));
    }

    @Test
    void toStringUsesExpectedFormat() {
        Book book = new Book("Тест", "Автор", 100, 12.5);
        assertEquals("Тест | Автор | 100 стор. | 12.50 грн", book.toString());
    }

    @Test
    void constructorRejectsNullTitleAndAuthor() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book(null, "Автор", 100, 10));
        assertThrows(IllegalArgumentException.class,
                () -> new Book("Назва", null, 100, 10));
    }

    @Test
    void constructorAcceptsZeroPrice() {
        assertEquals(0.0, new Book("Назва", "Автор", 100, 0).getPrice(), 0.0001);
    }

    @Test
    void constructorRejectsInfinity() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book("Назва", "Автор", 100, Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class,
                () -> new Book("Назва", "Автор", 100, Double.NEGATIVE_INFINITY));
    }

    @Test
    void fromCsvAcceptsCommaAsDecimalSeparator() {
        assertEquals(250.50, Book.fromCsv("Кобзар;Шевченко;320;250,50").getPrice(), 0.0001);
    }

    @Test
    void builderCreatesValidBook() {
        Book book = Book.builder()
                .title("Кобзар").author("Тарас Шевченко")
                .pages(320).price(250.50).build();
        assertEquals("Кобзар", book.getTitle());
        assertEquals(320, book.getPages());
    }

    @Test
    void builderDoesNotBypassValidation() {
        assertThrows(IllegalArgumentException.class,
                () -> Book.builder().title("Назва").author("Автор")
                        .pages(0).price(10).build());
        assertThrows(IllegalArgumentException.class,
                () -> Book.builder().author("Автор").pages(10).price(10).build());
    }
}
