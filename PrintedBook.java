/** Друкована книжка: умовна вартість читання дорівнює ціні примірника. */
public final class PrintedBook extends Book {
    /** Створює друковану книжку. */ public PrintedBook(String title,String author,int pages,double price){super(title,author,pages,price);}
    /** Створює книжку з роком і жанром. */ public PrintedBook(String title,String author,int pages,double price,Integer year,String genre){super(title,author,pages,price,year,genre);}
    /** Формат PRINTED. */ @Override public BookFormat getFormat(){return BookFormat.PRINTED;}
    /** Вартість придбання друкованого примірника. */ @Override public double readingCost(){return getPrice();}
}
