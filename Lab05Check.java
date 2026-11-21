import java.nio.file.*;
import java.util.*;
/** Автономна перевірка лабораторної №5 без Maven. */
public class Lab05Check {
    static int checks=0;
    static void ok(boolean value){checks++;if(!value)throw new AssertionError("Помилка перевірки "+checks);}
    static void fail(Checked action){checks++;try{action.run();throw new AssertionError("Очікувалась помилка");}catch(CsvExportException expected){}}
    interface Checked {void run() throws CsvExportException;}
    public static void main(String[] args)throws Exception {
        Repository<BookRecord> r=new Repository<>();List<BookRecord> source=SampleBooks.records();
        for(BookRecord b:source)r.add(b);
        ok(r.size()==6);List<BookRecord> snapshot=r.all();ok(snapshot.equals(source));ok(r.find(b->b.getYear()>=2020).size()==5);
        CsvExporter<BookRecord> exporter=new CsvExporter<>(BookRecord.class);
        ok(exporter.header().equals("автор,жанр,сторінки,назва,рік"));ok(exporter.format(List.of()).equals(exporter.header()+"\n"));
        Path p=Files.createTempFile("lab05", ".csv");
        try{
            exporter.write(p,source);String content=Files.readString(p);ok(content.contains("\"Місто, яке читає\""));
            ok(content.contains("\"Книга \"\"Мрія\"\"\""));ok(content.contains("\"Дорога\nдодому\""));
            ok(BookCsvImporter.read(p).equals(source));
            exporter.write(p,List.of());ok(BookCsvImporter.read(p).isEmpty());
            Files.writeString(p,exporter.header()+"\na,b,5,c\n");fail(()->BookCsvImporter.read(p));
            Files.writeString(p,exporter.header()+"\na,b,abc,c,2022\n");fail(()->BookCsvImporter.read(p));
            Files.writeString(p,exporter.header()+"\na,b,5,c,not-year\n");fail(()->BookCsvImporter.read(p));
            Files.writeString(p,exporter.header()+"\n\"unclosed\n");fail(()->BookCsvImporter.read(p));
            Files.writeString(p,"");fail(()->BookCsvImporter.read(p));
        }finally{Files.deleteIfExists(p);}
        fail(()->BookCsvImporter.read(Path.of("missing-lab05-unknown.csv")));
        ok(source.get(0).equals(new BookRecord("Місто, яке читає","Олена Коваль",2022,320,"Роман")));
        System.out.println("Усі перевірки пройдено ("+checks+")");
    }
}
