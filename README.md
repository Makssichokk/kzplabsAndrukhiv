# Лабораторна №4 — Потокова обробка даних у Java-проєкті
Варіант 1, Каталог книжок. База: лабораторна №3 (Book, PrintedBook, ElectronicBook, BookFormat).

## Сумісність
Старий CSV `title;author;pages;price` читається без змін. Для перевірки завдання №4 додано розширений формат `title;author;pages;price;year;genre` у `data/input-lab04.csv`. Рік і жанр для старих записів невідомі (`null`); фільтр 2020+ їх пропускає, групування відносить до «Не вказано». Основний звіт не змінено, запити доступні тільки за `--queries`.

## Запуск
```bash
javac -encoding UTF-8 -d out/classes src/main/java/*.java
java -cp out/classes Main --input data/input.csv
java -cp out/classes Main --input data/input-lab04.csv --queries --find "Кобзар"
mvn test
```

## Запити
`BookQueries.recent` — filter year >= 2020; `titles` — map; `countByGenre` — groupingBy(TreeMap, counting); `pageStatistics` — summarizingInt; `top` — sorted by pages DESC then title ASC, limit(N); `findByTitle` — Optional<Book>.
Потокові методи не мутують вхідний список; `top(-1)` відхиляється. Попередні JUnit-тести збережені, додано `BookQueriesTest`.

## Перевірка
Java 21: компіляція і запуск контрольного набору успішні. Maven у середовищі відсутній, тому JUnit і GitHub CI потрібно запустити у власному репозиторії. Workflow з попередньої роботи збережено. GitHub Issue і Pull Request створюються користувачем після завантаження гілки `lab04`.
