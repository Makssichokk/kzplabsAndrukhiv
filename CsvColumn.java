import java.lang.annotation.*;
/** Назва CSV-стовпця для поля, доступна під час виконання. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvColumn { /** Назва стовпця. @return назва */ String value(); }
