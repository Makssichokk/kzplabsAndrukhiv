/** Електронна книжка: умовна вартість читання — 80% ціни ліцензії. */
public final class ElectronicBook extends Book {
    /** Створює електронну книжку. */ public ElectronicBook(String title,String author,int pages,double price){super(title,author,pages,price);}
    /** Формат ELECTRONIC. */ @Override public BookFormat getFormat(){return BookFormat.ELECTRONIC;}
    /** Вартість читання за навчальним коефіцієнтом 0.8. */ @Override public double readingCost(){return getPrice()*0.8;}
}
