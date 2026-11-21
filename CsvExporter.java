import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
/** Універсальний рефлексивний експортер будь-якого анотованого типу. @param <T> тип запису */
public final class CsvExporter<T> {
    private final Class<T> type;
    private final List<Field> fields;
    /** Готує стовпці за Java-іменами полів. @param type клас запису @throws CsvExportException некоректні метадані */
    public CsvExporter(Class<T> type) throws CsvExportException {
        this.type=Objects.requireNonNull(type);
        fields=Arrays.stream(type.getDeclaredFields()).filter(f->!Modifier.isStatic(f.getModifiers())&&f.isAnnotationPresent(CsvColumn.class)).sorted(Comparator.comparing(Field::getName)).toList();
        if(fields.isEmpty())throw new CsvExportException("Немає полів із @CsvColumn: "+type.getName());
        Set<String> headers=new HashSet<>();
        for(Field f:fields){
            String name=f.getAnnotation(CsvColumn.class).value();
            if(name.isBlank()||!headers.add(name))throw new CsvExportException("Порожня або повторна назва стовпця: "+name);
            try { if(!f.trySetAccessible())throw new CsvExportException("Немає доступу до поля: "+f.getName()); }
            catch(SecurityException ex){throw new CsvExportException("Немає доступу до поля: "+f.getName(),ex);}
        }
    }
    /** Повертає детермінований заголовок. @return заголовок */
    public String header(){return fields.stream().map(f->escape(f.getAnnotation(CsvColumn.class).value())).collect(Collectors.joining(","));}
    /** Перетворює список на CSV, включно із заголовком для порожнього списку. @param items записи @return CSV @throws CsvExportException помилка доступу */
    public String format(List<? extends T> items) throws CsvExportException {
        StringBuilder result=new StringBuilder(header()).append('\n');
        for(T item:items){
            if(item==null||!type.isInstance(item))throw new CsvExportException("Неправильний тип або null у списку");
            List<String> cells=new ArrayList<>();
            for(Field field:fields){
                try {Object value=field.get(item);cells.add(escape(value==null?"":String.valueOf(value)));}
                catch(IllegalAccessException | IllegalArgumentException ex){throw new CsvExportException("Не вдалося прочитати поле "+field.getName(),ex);}
            }
            result.append(String.join(",",cells)).append('\n');
        }
        return result.toString();
    }
    /** Записує UTF-8 CSV через Path і Files. @param path шлях @param items записи @throws CsvExportException помилка */
    public void write(Path path,List<? extends T> items) throws CsvExportException {
        String content=format(items);
        try {Files.writeString(path,content,StandardCharsets.UTF_8);}
        catch(IOException ex){throw new CsvExportException("Не вдалося записати CSV: "+path,ex);}
    }
    /** CSV-екранування ком, лапок і перенесень. @param value значення @return екрановане */
    public static String escape(String value){if(value.contains(",")||value.contains("\"")||value.contains("\n")||value.contains("\r"))return "\""+value.replace("\"","\"\"")+"\"";return value;}
}
