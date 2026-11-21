# Лабораторна робота №5 — Java, варіант 1 «Бібліотека»

## Попередня сумісність
Проєкт побудовано на лабораторній №4. Збережено `Book`, `PrintedBook`, `ElectronicBook`, `BookFormat`, `BookQueries`, старий вхідний формат `title;author;pages;price`, підтримку шести полів і стандартний текстовий звіт. Старі JUnit-тести залишені в `src/test/java`.

## Нова модель
- `Repository<T>` — типобезпечне сховище: `add`, `all` (незмінний знімок), `find(Predicate<? super T>)`, `size`.
- `BookRecord` — незалежний незмінний експортний запис із `title:String`, `author:String`, `year:int`, `pages:int`, `genre:String`. Ключ відновлення: `title + author`; рівність перевіряє всі поля.
- `@CsvColumn` — `RUNTIME`, `FIELD`, `value()` з назвою колонки.
- `CsvExporter<T>` — універсальний рефлексивний експорт без посилань на конкретний предметний клас; сортує Java-імена полів.
- `CsvParser` — документний парсер, підтримує коми, лапки, `\r\n` та multiline.
- `BookCsvImporter` — предметна валідація і відновлення; `CsvExportException` зберігає `cause`.

## CSV
Порядок полів за Java-іменами: `author, genre, pages, title, year`.
Заголовок: `автор,жанр,сторінки,назва,рік`.
Дані: `data/input-lab05.csv` (6 записів), `data/empty-lab05.csv` (лише заголовок), `data/bad-lab05.csv` (хибне число).
CSV лабораторної №5 — **окремий експортний формат**, він не замінює старий `data/input.csv`.

## Команди
```bash
javac -encoding UTF-8 -d out/classes src/main/java/*.java
java -cp out/classes Main --export out/books.csv
java -cp out/classes Main --import data/input-lab05.csv
javac -encoding UTF-8 -cp out/classes -d out/classes checks/Lab05Check.java
java -cp out/classes Lab05Check
mvn test
```
Папку `out` створити перед запуском експорту: `mkdir -p out/classes`.
За відсутності Maven доступна автономна перевірка `Lab05Check`.

## Результати локальної перевірки
Компіляція `javac` пройшла; `Main --export` повідомив «Експортовано записів: 6», «Round-trip: списки рівні»; `Main --import` прочитав 6 записів; `Lab05Check` пройшов 17 перевірок. Maven/JUnit і CI не запускалися в поточному середовищі, їхній статус не підтверджено.

## GitHub
Для виконання вимоги щодо Issues і Pull Request створіть issue з описом round-trip, гілку `lab05`, push і PR до `main`. Шаблони розміщено в `docs/GITHUB.md`. Реальне створення issue/PR у вашому репозиторії ще не виконувалося.
