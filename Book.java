import java.util.Locale;
import java.util.Objects;
/** Абстрактна незмінна книжка з загальними полями й перевірками. */
public abstract class Book {
    private final String title, author;
    private final PagePrice pagePrice;
    private final Integer year;
    private final String genre;
    /** Створює книжку і перевіряє інваріанти. */
    protected Book(String title, String author, int pages, double price) {
        this(title,author,pages,price,null,null);
    }
    /** Створює книжку з необов’язковими метаданими року й жанру. */
    protected Book(String title, String author, int pages, double price, Integer year, String genre) {
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Назва книжки відсутня.");
        if (author == null || author.trim().isEmpty()) throw new IllegalArgumentException("Автор книжки відсутній.");
        this.title=title.trim(); this.author=author.trim(); this.pagePrice=new PagePrice(pages,price);
        if(year!=null && (year<1450 || year>2100)) throw new IllegalArgumentException("Некоректний рік видання.");
        if(genre!=null && genre.isBlank()) throw new IllegalArgumentException("Жанр не може бути порожнім.");
        this.year=year; this.genre=genre==null?null:genre.trim();
    }
    /** Читає старий чотирипольний CSV як друковану книжку. */
    public static Book fromCsv(String line) { return BookFactory.fromCsv(line); }
    /** Повертає назву. */ public final String getTitle(){return title;}
    /** Повертає автора. */ public final String getAuthor(){return author;}
    /** Повертає сторінки. */ public final int getPages(){return pagePrice.pages();}
    /** Повертає ціну. */ public final double getPrice(){return pagePrice.price();}
    /** Повертає об'єкт-значення сторінок і ціни. */ public final PagePrice getPagePrice(){return pagePrice;}
    /** Повертає рік видання або null, якщо старий CSV не містив року. */ public final Integer getYear(){return year;}
    /** Повертає жанр або null для старих записів. */ public final String getGenre(){return genre;}
    /** Повертає формат книжки. */ public abstract BookFormat getFormat();
    /** Обчислює умовну вартість читання у гривнях. */ public abstract double readingCost();
    /** Формат, сумісний із попереднім звітом. */
    @Override public final String toString(){return String.format(Locale.ROOT,"%s | %s | %d стор. | %.2f грн",title,author,getPages(),getPrice());}
    /** Рівність за підтипом, назвою та автором. */
    @Override public final boolean equals(Object o){if(this==o)return true; if(o==null||getClass()!=o.getClass())return false; Book b=(Book)o;return title.equals(b.title)&&author.equals(b.author);}
    /** Хеш за тими самими полями, що й equals. */
    @Override public final int hashCode(){return Objects.hash(getClass(),title,author);}
    /** Створює сумісний Builder друкованої книжки. */ public static Builder builder(){return new Builder();}
    /** Builder друкованої книжки для сумісності старого API. */
    public static final class Builder {
        private String title,author;private int pages;private double price;
        /** Задає назву. */ public Builder title(String v){title=v;return this;}
        /** Задає автора. */ public Builder author(String v){author=v;return this;}
        /** Задає сторінки. */ public Builder pages(int v){pages=v;return this;}
        /** Задає ціну. */ public Builder price(double v){price=v;return this;}
        /** Створює друковану книжку. */ public Book build(){return new PrintedBook(title,author,pages,price);}
    }
}
