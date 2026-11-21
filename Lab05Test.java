import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
/** Тести параметризованого сховища та CSV round-trip. */
public class Lab05Test {
    @Test void repositorySnapshotsAndPredicate(){
        Repository<BookRecord> r=new Repository<>();r.add(SampleBooks.records().get(0));
        List<BookRecord> snap=r.all();r.add(SampleBooks.records().get(1));
        assertEquals(1,snap.size());assertThrows(UnsupportedOperationException.class,()->snap.clear());
        assertEquals(1,r.find(b->b.getYear()>=2024).size());assertThrows(NullPointerException.class,()->r.add(null));
    }
    @Test void metadataAndGenericExporter() throws Exception {
        CsvExporter<BookRecord> e=new CsvExporter<>(BookRecord.class);
        assertEquals("автор,жанр,сторінки,назва,рік",e.header());
        assertEquals(e.header()+"\n",e.format(List.of()));
        assertEquals(java.lang.annotation.RetentionPolicy.RUNTIME,CsvColumn.class.getAnnotation(java.lang.annotation.Retention.class).value());
        assertEquals("x",new CsvExporter<>(Other.class).header());
    }
    static final class Other { @CsvColumn("x") private final int number=5; }
    @Test void roundTripEscapingAndMultiline() throws Exception {
        Path p=Files.createTempFile("lab05-", ".csv");
        try {var originals=SampleBooks.records();new CsvExporter<>(BookRecord.class).write(p,originals);
            String text=Files.readString(p);assertTrue(text.contains("\"Місто, яке читає\""));
            assertTrue(text.contains("\"Книга \"\"Мрія\"\"\""));assertTrue(text.contains("\"Дорога\nдодому\""));
            assertEquals(originals,BookCsvImporter.read(p));
        }finally{Files.deleteIfExists(p);}
    }
    @Test void emptyAndFailures() throws Exception {
        Path p=Files.createTempFile("lab05-", ".csv");
        try {new CsvExporter<>(BookRecord.class).write(p,List.of());assertTrue(BookCsvImporter.read(p).isEmpty());
            Files.writeString(p, "автор,жанр,сторінки,назва,рік\na,b,3,c\n");assertThrows(CsvExportException.class,()->BookCsvImporter.read(p));
            Files.writeString(p, "автор,жанр,сторінки,назва,рік\na,b,abc,c,2022\n");
            CsvExportException e=assertThrows(CsvExportException.class,()->BookCsvImporter.read(p));assertInstanceOf(NumberFormatException.class,e.getCause());
            Files.writeString(p, "автор,жанр,сторінки,назва,рік\na,b,3,c,abc\n");assertThrows(CsvExportException.class,()->BookCsvImporter.read(p));
            Files.writeString(p, "автор,жанр,сторінки,назва,рік\n\"broken\n");assertThrows(CsvExportException.class,()->BookCsvImporter.read(p));
            Files.writeString(p, "");assertThrows(CsvExportException.class,()->BookCsvImporter.read(p));
        }finally{Files.deleteIfExists(p);}
        assertInstanceOf(java.io.IOException.class,assertThrows(CsvExportException.class,()->BookCsvImporter.read(Path.of("missing-lab05-unknown.csv"))).getCause());
        Path dir=Files.createTempDirectory("lab05-dir-");
        try{assertInstanceOf(java.io.IOException.class,assertThrows(CsvExportException.class,()->new CsvExporter<>(BookRecord.class).write(dir,SampleBooks.records())).getCause());}
        finally{Files.deleteIfExists(dir);}
    }
    @Test void equalityAndKey(){var b=SampleBooks.records().get(0);assertEquals(b,new BookRecord(b.getTitle(),b.getAuthor(),b.getYear(),b.getPages(),b.getGenre()));assertEquals("Місто, яке читає | Олена Коваль",b.key());}
}
