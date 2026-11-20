/**
 * Допоміжне незмінне значення, яке зберігає кількість сторінок і ціну книжки.
 *
 * @param pages кількість сторінок
 * @param price ціна
 */
public record PagePrice(int pages, double price) {
    public PagePrice {
        if (pages <= 0) {
            throw new IllegalArgumentException(
                    "Кількість сторінок має бути більшою за 0.");
        }
        if (!Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException(
                    "Ціна має бути невід'ємним скінченним числом.");
        }
    }
}
