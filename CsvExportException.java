/** Перевірювана помилка експорту, імпорту або структури CSV. */
public class CsvExportException extends Exception {
    /** Помилка з поясненням. @param message опис */
    public CsvExportException(String message) { super(message); }
    /** Помилка зі збереженою причиною. @param message опис @param cause причина */
    public CsvExportException(String message, Throwable cause) { super(message,cause); }
}
