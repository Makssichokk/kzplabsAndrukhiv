import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Консольна програма для читання, перевірки й аналізу каталогу книжок.
 * Варіант 1. Дані зберігаються у CSV-файлі з роздільником ';'.
 */
public class Main {
    private static final String DEFAULT_INPUT = "data/input.csv";
    private static final String DEFAULT_OUTPUT = "data/report.txt";

    /** Головний метод програми. */
    public static void main(String[] args) {
        Path input = Paths.get(DEFAULT_INPUT);
        Path output = Paths.get(DEFAULT_OUTPUT);
        boolean readingCosts = false;
        boolean queries = false;
        String findTitle = null;
        Path exportPath = null, importPath = null;

        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--export" -> { if(i+1>=args.length)throw new IllegalArgumentException("Не вказано шлях після --export");exportPath=Paths.get(args[++i]); }
                    case "--import" -> { if(i+1>=args.length)throw new IllegalArgumentException("Не вказано шлях після --import");importPath=Paths.get(args[++i]); }
                    case "--reading-costs" -> readingCosts = true;
                    case "--queries" -> queries = true;
                    case "--find" -> {if(i+1>=args.length)throw new IllegalArgumentException("Не вказано назву після --find");findTitle=args[++i];queries=true;}
                    case "--help" -> {
                        printHelp();
                        return;
                    }
                    case "--version" -> {
                        String version = Main.class.getPackage().getImplementationVersion();
                        System.out.println("Каталог книжок, версія "
                                + (version != null ? version : "3.0.0"));
                        return;
                    }
                    case "--input" -> {
                        if (i + 1 >= args.length) {
                            throw new IllegalArgumentException("Не вказано шлях після --input");
                        }
                        input = Paths.get(args[++i]);
                    }
                    case "--output" -> {
                        if (i + 1 >= args.length) {
                            throw new IllegalArgumentException("Не вказано шлях після --output");
                        }
                        output = Paths.get(args[++i]);
                    }
                    default -> throw new IllegalArgumentException("Невідомий аргумент: " + args[i]);
                }
            }
        } catch (IllegalArgumentException exception) {
            System.out.println("Помилка: " + exception.getMessage());
            printHelp();
            return;
        }

        if(exportPath!=null || importPath!=null){
            try{runCsvOperations(exportPath,importPath);}catch(CsvExportException ex){System.out.println("Помилка CSV: "+ex.getMessage());if(ex.getCause()!=null)System.out.println("Причина: "+ex.getCause());}
            return;
        }
        runProgram(input, output, readingCosts,queries,findTitle);
    }

    private static void printHelp() {
        System.out.println("КАТАЛОГ КНИЖОК — варіант 1");
        System.out.println("Використання: java Main [параметри]");
        System.out.println("--help              Показати довідку");
        System.out.println("--reading-costs     Додати поліморфні вартості читання");
        System.out.println("--queries           Додати запити Stream API");
        System.out.println("--find <назва>      Пошук за назвою через Optional");
        System.out.println("--export <шлях>    Експорт демонстраційних BookRecord і round-trip");
        System.out.println("--import <шлях>    Імпорт BookRecord з CSV");
        System.out.println("--version           Показати версію програми");
        System.out.println("--input <шлях>      Вказати вхідний CSV-файл");
        System.out.println("--output <шлях>     Вказати файл звіту");
    }

    /** Виконує експорт і/або імпорт без зміни старого звіту. */
    private static void runCsvOperations(Path export,Path imported) throws CsvExportException {
        if(export!=null){
            Repository<BookRecord> repo=new Repository<>();
            for(BookRecord b:SampleBooks.records())repo.add(b);
            new CsvExporter<>(BookRecord.class).write(export,repo.all());
            List<BookRecord> restored=BookCsvImporter.read(export);
            System.out.println("Експортовано записів: "+repo.size());
            System.out.println("Round-trip: "+(repo.all().equals(restored)?"списки рівні":"списки НЕ рівні"));
            if(!repo.all().equals(restored))throw new CsvExportException("Round-trip не пройдено");
        }
        if(imported!=null){
            List<BookRecord> books=BookCsvImporter.read(imported);
            System.out.println("Імпортовано записів: "+books.size());
            for(BookRecord b:books)System.out.println(b);
        }
    }

    private static void runProgram(Path input, Path output, boolean readingCosts,boolean queries,String findTitle) {
        List<Book> books = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        if (!Files.exists(input)) {
            System.out.println("Вхідний файл не знайдено: " + input);
            System.out.println("Створіть файл або вкажіть інший шлях через --input.");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(input, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                try {
                    books.add(Book.fromCsv(line));
                } catch (IllegalArgumentException exception) {
                    errors.add("Рядок " + (i + 1) + ": " + exception.getMessage());
                }
            }
        } catch (IOException exception) {
            System.out.println("Помилка читання файлу: " + exception.getMessage());
            return;
        }

        String report = createReport(books, errors);
        if (readingCosts) report += createReadingCosts(books);
        if (queries) report += createQueries(books,findTitle);
        System.out.print(report);

        try {
            Path parent = output.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(output, report, StandardCharsets.UTF_8);
            System.out.println("\nЗвіт збережено у файл: " + output);
        } catch (IOException exception) {
            System.out.println("Не вдалося зберегти звіт: " + exception.getMessage());
        }
    }

    /**
     * Формує звіт із книжок та повідомлень про некоректні рядки.
     *
     * @param books список перевірених книжок
     * @param errors список повідомлень про помилки
     * @return текст звіту
     */
    public static String createReport(List<Book> books, List<String> errors) {
        StringBuilder report = new StringBuilder();
        var statistics=BookQueries.pageStatistics(books);
        double totalPrice=books.stream().mapToDouble(Book::getPrice).sum();
        Book mostExpensive=books.stream().max(java.util.Comparator.comparingDouble(Book::getPrice)).orElse(null);

        report.append("========== КАТАЛОГ КНИЖОК ==========\n");
        report.append("Кількість коректних книжок: ")
                .append(books.size()).append("\n\n");

        for (Book book : books) {
            report.append(book).append("\n");
        }

        double averagePages = statistics.getAverage();
        report.append("\n========== РЕЗУЛЬТАТИ ==========\n");
        report.append(String.format(Locale.ROOT,
                "1. Середня кількість сторінок: %.2f%n", averagePages));

        if (mostExpensive == null) {
            report.append("2. Найдорожча книжка: немає даних\n");
        } else {
            report.append("2. Найдорожча книжка: ").append(mostExpensive.getTitle()).append("\n");
            report.append("   Автор: ").append(mostExpensive.getAuthor()).append("\n");
            report.append(String.format(Locale.ROOT,
                    "   Ціна: %.2f грн%n", mostExpensive.getPrice()));
        }

        report.append(String.format(Locale.ROOT,
                "3. Сумарна вартість каталогу: %.2f грн%n", totalPrice));
        report.append("\n========== ПЕРЕВІРКА ДАНИХ ==========\n");

        if (errors.isEmpty()) {
            report.append("Помилок не виявлено.\n");
        } else {
            for (String error : errors) {
                report.append(error).append("\n");
            }
        }
        return report.toString();
    }
    /** Поліморфний додатковий розділ без перевірки конкретного підтипу. */
    public static String createReadingCosts(List<? extends Book> books) {
        StringBuilder sb=new StringBuilder("\n========== ВАРТІСТЬ ЧИТАННЯ ==========\n");
        for(Book book:books) sb.append(String.format(Locale.ROOT,"%s (%s): %.2f грн%n",book.getTitle(),book.getFormat().label(),book.readingCost()));
        return sb.toString();
    }
    /** Формує додатковий розділ запитів, не змінюючи основний звіт. */
    public static String createQueries(List<Book> books,String findTitle){
        StringBuilder sb=new StringBuilder("\n========== ЗАПИТИ STREAM API ==========\n");
        sb.append("Книги від 2020 року: ").append(BookQueries.recent(books).stream().map(Book::getTitle).collect(java.util.stream.Collectors.joining(", "))).append("\n");
        sb.append("Назви: ").append(String.join(", ",BookQueries.titles(books))).append("\n");
        sb.append("Кількість за жанрами: ").append(BookQueries.countByGenre(books)).append("\n");
        var st=BookQueries.pageStatistics(books);
        if(st.getCount()==0)sb.append("Статистика сторінок: немає даних\n");
        else sb.append(String.format(Locale.ROOT,"Статистика сторінок: кількість=%d, сума=%d, мінімум=%d, середнє=%.2f, максимум=%d%n",st.getCount(),st.getSum(),st.getMin(),st.getAverage(),st.getMax()));
        sb.append("Топ-5 за сторінками:\n");
        int rank=1;for(Book b:BookQueries.top(books,5))sb.append(rank++).append(". ").append(b.getTitle()).append(" (").append(b.getPages()).append(" стор.)\n");
        if(findTitle!=null)sb.append("Пошук \""+findTitle+"\": ").append(BookQueries.findByTitle(books,findTitle).map(Book::toString).orElse("не знайдено")).append("\n");
        return sb.toString();
    }

}
