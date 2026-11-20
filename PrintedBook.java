/** Друкована книжка: умовна вартість читання дорівнює ціні примірника. */
public final class PrintedBook extends Book {
    /** Створює друковану книжку. */ public PrintedBook(String title,String author,int pages,double price){super(title,author,pages,price);}
    /** Формат PRINTED. */ @Override public BookFormat getFormat(){return BookFormat.PRINTED;}
    /** Вартість придбання друкованого примірника. */ @Override public double readingCost(){return getPrice();}
}
