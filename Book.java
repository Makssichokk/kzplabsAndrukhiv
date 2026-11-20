import java.util.Locale;

/**
 * Незмінна сутність книжки з каталогу (варіант 12).
 * Містить назву, автора, кількість сторінок і ціну.
 */
public final class Book {
    private final String title;
    private final String author;
    private final PagePrice pagePrice;

    /**
     * Створює книжку та перевіряє всі її інваріанти.
     *
     * @param title назва книжки, не може бути порожньою
     * @param author автор, не може бути порожнім
     * @param pages кількість сторінок, має бути більшою за нуль
     * @param price ціна, має бути скінченним невід'ємним числом
     * @throws IllegalArgumentException якщо хоча б одне значення некоректне
     */
    public Book(String title, String author, int pages, double price) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва книжки відсутня.");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Автор книжки відсутній.");
        }
        this.title = title.trim();
        this.author = author.trim();
        this.pagePrice = new PagePrice(pages, price);
    }

    /**
     * Створює об'єкт Book з одного CSV-рядка.
     * Формат: title;author;pages;price.
     *
     * @param line рядок CSV
     * @return створена книжка
     * @throws IllegalArgumentException якщо формат або дані некоректні
     */
    public static Book fromCsv(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Порожній рядок.");
        }

        String[] fields = line.split(";", -1);
        if (fields.length != 4) {
            throw new IllegalArgumentException("Неправильна кількість полів.");
        }

        String title = fields[0].trim();
        String author = fields[1].trim();
        if (title.isEmpty()) {
            throw new IllegalArgumentException("Назва книжки відсутня.");
        }
        if (author.isEmpty()) {
            throw new IllegalArgumentException("Автор книжки відсутній.");
        }

        try {
            int pages = Integer.parseInt(fields[2].trim());
            double price = Double.parseDouble(fields[3].trim().replace(',', '.'));
            return new Book(title, author, pages, price);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Неправильний формат числових даних.");
        }
    }

    /** @return назва книжки */
    public String getTitle() {
        return title;
    }

    /** @return автор книжки */
    public String getAuthor() {
        return author;
    }

    /** @return кількість сторінок */
    public int getPages() {
        return pagePrice.pages();
    }

    /** @return ціна книжки */
    public double getPrice() {
        return pagePrice.price();
    }

    /** @return незмінне значення з кількістю сторінок і ціною */
    public PagePrice getPagePrice() {
        return pagePrice;
    }

    /**
     * Повертає текстове представлення книжки.
     *
     * @return відформатований запис книжки
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s | %s | %d стор. | %.2f грн",
                title, author, getPages(), getPrice());
    }

    /** @return новий Builder для створення книжки */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder для створення Book. Валідація виконується в конструкторі Book. */
    public static final class Builder {
        private String title;
        private String author;
        private int pages;
        private double price;

        /** @param title назва книжки @return цей Builder */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /** @param author автор @return цей Builder */
        public Builder author(String author) {
            this.author = author;
            return this;
        }

        /** @param pages кількість сторінок @return цей Builder */
        public Builder pages(int pages) {
            this.pages = pages;
            return this;
        }

        /** @param price ціна @return цей Builder */
        public Builder price(double price) {
            this.price = price;
            return this;
        }

        /**
         * Створює книжку через основний конструктор.
         *
         * @return створена книжка
         * @throws IllegalArgumentException якщо дані некоректні
         */
        public Book build() {
            return new Book(title, author, pages, price);
        }
    }
}
