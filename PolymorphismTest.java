import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
/** Нові тести ієрархії, enum, поліморфізму й рівності. */
class PolymorphismTest {
    @Test void differentCosts(){Book a=new PrintedBook("A","B",100,100);Book b=new ElectronicBook("A","B",100,100);assertEquals(100,a.readingCost());assertEquals(80,b.readingCost(),1e-9);}
    @Test void formats(){assertEquals("друкована",BookFormat.PRINTED.label());assertEquals("електронна",BookFormat.ELECTRONIC.label());}
    @Test void polymorphicCollection(){List<Book> books=List.of(new PrintedBook("A","B",100,100),new ElectronicBook("C","D",100,100));assertEquals(180,books.stream().mapToDouble(Book::readingCost).sum(),1e-9);assertTrue(Main.createReadingCosts(books).contains("електронна"));}
    @Test void equalityAndHashSet(){Book a=new PrintedBook("A","B",100,10);Book b=new PrintedBook("A","B",200,20);Book c=new ElectronicBook("A","B",100,10);assertEquals(a,b);assertEquals(a.hashCode(),b.hashCode());assertNotEquals(a,c);assertNotEquals(a,null);assertEquals(2,new HashSet<>(List.of(a,b,c)).size());}
    @Test void baseInvariants(){assertThrows(IllegalArgumentException.class,()->new ElectronicBook("", "B",10,10));assertThrows(IllegalArgumentException.class,()->new PrintedBook("A","B",0,10));assertThrows(IllegalArgumentException.class,()->new ElectronicBook("A","B",10,Double.NaN));}
    @Test void factoryPreservesCsv(){Book b=Book.fromCsv("A;B;100;10,50");assertInstanceOf(PrintedBook.class,b);assertEquals(10.5,b.getPrice());assertInstanceOf(ElectronicBook.class,BookFactory.fromCsv("A;B;100;10",BookFormat.ELECTRONIC));}
    @Test void oldReportUnchangedForSubtypes(){List<Book> a=List.of(new PrintedBook("A","B",100,10));List<Book> b=List.of(new ElectronicBook("A","B",100,10));assertEquals(Main.createReport(a,List.of()),Main.createReport(b,List.of()));}
}
