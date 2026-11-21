import java.util.Objects;
/** Незмінний бібліотечний запис для round-trip (варіант 1). */
public final class BookRecord {
    @CsvColumn("назва") private final String title;
    @CsvColumn("автор") private final String author;
    @CsvColumn("рік") private final int year;
    @CsvColumn("сторінки") private final int pages;
    @CsvColumn("жанр") private final String genre;
    /** Створює перевірений запис. @param title назва @param author автор @param year рік @param pages сторінки @param genre жанр */
    public BookRecord(String title,String author,int year,int pages,String genre) {
        if(title==null||title.isBlank())throw new IllegalArgumentException("Назва не може бути порожньою");
        if(author==null||author.isBlank())throw new IllegalArgumentException("Автор не може бути порожнім");
        if(year<1450||year>2100)throw new IllegalArgumentException("Рік має бути від 1450 до 2100");
        if(pages<=0)throw new IllegalArgumentException("Сторінок має бути більше нуля");
        if(genre==null||genre.isBlank())throw new IllegalArgumentException("Жанр не може бути порожнім");
        this.title=title;this.author=author;this.year=year;this.pages=pages;this.genre=genre;
    }
    /** @return назва */ public String getTitle(){return title;}
    /** @return автор */ public String getAuthor(){return author;}
    /** @return рік */ public int getYear(){return year;}
    /** @return сторінки */ public int getPages(){return pages;}
    /** @return жанр */ public String getGenre(){return genre;}
    /** Ключ відновлення. @return назва та автор */ public String key(){return title+" | "+author;}
    /** Створює запис із книги попередньої моделі. @param book книжка @return запис */
    public static BookRecord from(Book book){
        if(book.getYear()==null||book.getGenre()==null)throw new IllegalArgumentException("Для експорту потрібні рік і жанр");
        return new BookRecord(book.getTitle(),book.getAuthor(),book.getYear(),book.getPages(),book.getGenre());
    }
    /** Розбирає CSV-значення у порядку Java-полів: author, genre, pages, title, year. @param cells комірки @return запис */
    public static BookRecord fromCsv(java.util.List<String> cells){
        if(cells.size()!=5)throw new IllegalArgumentException("Очікується 5 полів, знайдено "+cells.size());
        return new BookRecord(cells.get(3),cells.get(0),Integer.parseInt(cells.get(4)),Integer.parseInt(cells.get(2)),cells.get(1));
    }
    /** Рівність за всіма п'ятьма полями. */
    @Override public boolean equals(Object o){if(this==o)return true;if(!(o instanceof BookRecord b))return false;return year==b.year&&pages==b.pages&&title.equals(b.title)&&author.equals(b.author)&&genre.equals(b.genre);}
    /** Узгоджений хеш. */ @Override public int hashCode(){return Objects.hash(title,author,year,pages,genre);}
    /** Представлення для діагностики. */ @Override public String toString(){return key()+" ("+year+", "+pages+", "+genre+")";}
}
