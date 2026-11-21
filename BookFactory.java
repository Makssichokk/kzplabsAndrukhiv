/** Фабрика об'єктів із сумісного CSV або з явно заданого формату. */
public final class BookFactory {
    private BookFactory(){}
    /** Читає старий CSV title;author;pages;price, типово PRINTED. */
    public static Book fromCsv(String line){return fromCsv(line,BookFormat.PRINTED);}
    /** Читає старий CSV, задаючи формат поза CSV. */
    public static Book fromCsv(String line,BookFormat format){
        if(line==null||line.trim().isEmpty())throw new IllegalArgumentException("Порожній рядок.");
        String[] f=line.split(";",-1);
        if(f.length!=4 && f.length!=6)throw new IllegalArgumentException("Неправильна кількість полів.");
        if(f[0].trim().isEmpty())throw new IllegalArgumentException("Назва книжки відсутня.");
        if(f[1].trim().isEmpty())throw new IllegalArgumentException("Автор книжки відсутній.");
        if(format==null)throw new IllegalArgumentException("Формат книжки відсутній.");
        try {
            int pages=Integer.parseInt(f[2].trim());double price=Double.parseDouble(f[3].trim().replace(',','.'));
            Integer year=f.length==6?Integer.parseInt(f[4].trim()):null;
            String genre=f.length==6?f[5]:null;
            return switch(format){case PRINTED->new PrintedBook(f[0],f[1],pages,price,year,genre);case ELECTRONIC->new ElectronicBook(f[0],f[1],pages,price,year,genre);};
        }catch(NumberFormatException e){throw new IllegalArgumentException("Неправильний формат числових даних.");}
    }
}
