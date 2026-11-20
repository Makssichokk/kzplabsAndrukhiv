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

        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--reading-costs" -> readingCosts = true;
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

        runProgram(input, output, readingCosts);
    }

    private static void printHelp() {
        System.out.println("КАТАЛОГ КНИЖОК — варіант 1");
        System.out.println("Використання: java Main [параметри]");
        System.out.println("--help              Показати довідку");
        System.out.println("--reading-costs     Додати поліморфні вартості читання");
        System.out.println("--version           Показати версію програми");
        System.out.println("--input <шлях>      Вказати вхідний CSV-файл");
        System.out.println("--output <шлях>     Вказати файл звіту");
    }

    private static void runProgram(Path input, Path output, boolean readingCosts) {
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
        long totalPages = 0;
        double totalPrice = 0.0;
        Book mostExpensive = null;

        report.append("========== КАТАЛОГ КНИЖОК ==========\n");
        report.append("Кількість коректних книжок: ")
                .append(books.size()).append("\n\n");

        for (Book book : books) {
            totalPages += book.getPages();
            totalPrice += book.getPrice();
            if (mostExpensive == null || book.getPrice() > mostExpensive.getPrice()) {
                mostExpensive = book;
            }
            report.append(book).append("\n");
        }

        double averagePages = books.isEmpty() ? 0.0 : (double) totalPages / books.size();
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
}
