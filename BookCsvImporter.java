import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
/** Імпортує бібліотечні записи з перевіреного CSV. */
public final class BookCsvImporter {
    private BookCsvImporter(){}
    /** Читає UTF-8 CSV і відновлює BookRecord. @param path шлях @return записи @throws CsvExportException помилка */
    public static List<BookRecord> read(Path path) throws CsvExportException {
        String content;
        try{content=Files.readString(path,StandardCharsets.UTF_8);}catch(IOException ex){throw new CsvExportException("Не вдалося прочитати CSV: "+path,ex);}
        List<List<String>> rows=CsvParser.parse(content);
        if(rows.isEmpty())throw new CsvExportException("Порожній CSV: немає заголовка");
        String expected=new CsvExporter<>(BookRecord.class).header();
        List<List<String>> heading=CsvParser.parse(expected+"\n");
        if(!rows.get(0).equals(heading.get(0)))throw new CsvExportException("Хибний заголовок; очікується: "+expected);
        List<BookRecord> books=new ArrayList<>();
        for(int i=1;i<rows.size();i++){
            try{books.add(BookRecord.fromCsv(rows.get(i)));}
            catch(RuntimeException ex){throw new CsvExportException("Запис "+i+": "+ex.getMessage(),ex);}
        }
        return List.copyOf(books);
    }
}
