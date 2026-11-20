# Каталог книжок — лабораторна робота № 2

## Варіант і предметна область

**Варіант 12. Предметна область — каталог книжок.** Клас-сутність: `Book`. Допоміжний незмінний record: `PagePrice`.

## Структура проєкту

```text
Katalog_Knyzhok_Lab2/
├── pom.xml
├── data/
│   └── input.csv
├── src/
│   ├── main/java/
│   │   ├── Main.java
│   │   ├── Book.java
│   │   └── PagePrice.java
│   └── test/java/
│       ├── BookTest.java
│       ├── MainTest.java
│       └── PagePriceTest.java
├── .github/workflows/ci.yml
├── .gitignore
├── README.md
└── REPORT.md
```

## Модель даних

- `Book` — незмінний клас-сутність. Поля `title`, `author` і `pagePrice` оголошені `private final`. Відкритих сеттерів немає.
- `Book.Builder` — вкладений builder; `build()` викликає основний конструктор, тому валідацію не обходить.
- `PagePrice` — record, який зберігає кількість сторінок і ціну та перевіряє їхні обмеження.
- `Book.fromCsv(String)` створює книжку з одного рядка CSV. Розбір і правила валідації зосереджені в одному місці — фабричному методі та конструкторі/record.

## Правила валідації

1. Рядок не повинен бути порожнім.
2. Має бути рівно чотири поля, розділені `;`.
3. Назва та автор не можуть бути порожніми.
4. `pages` має бути цілим числом більшим за 0.
5. `price` має бути скінченним числом, більшим або рівним 0.
6. Десятковий роздільник у ціні може бути крапкою або комою.

Некоректні рядки не додаються до каталогу. Програма записує номер рядка та причину помилки у звіт.

## Показники звіту

- середня кількість сторінок;
- найдорожча книжка;
- сумарна вартість каталогу.

Зовнішній формат звіту збережено відповідно до попередньої роботи.

## Запуск у Visual Studio Code

Потрібні **JDK 17 або новіша версія** та розширення **Extension Pack for Java**. Для запуску тестів потрібен Maven.

1. Розпакуйте ZIP.
2. У VS Code виберіть **File → Open Folder** та відкрийте папку `Katalog_Knyzhok_Lab2`.
3. Дочекайтеся завантаження Maven-проєкту.
4. Відкрийте Terminal → New Terminal і виконайте:

```bash
mvn test
mvn package
```

Запуск програми:

```bash
java -cp target/classes Main
```

Довідка:

```bash
java -cp target/classes Main --help
```

Версія програми:

```bash
java -cp target/classes Main --version
```

Власні файли:

```bash
java -cp target/classes Main --input data/input.csv --output data/report.txt
```

Якщо команда `mvn` не розпізнається, встановіть Maven або скористайтеся підтримкою Maven у VS Code.

## Автоматична перевірка (CI)

Файл `.github/workflows/ci.yml` запускає `mvn clean verify` на Ubuntu, Windows і macOS (JDK 17) при push у `main`/`lab2` та при Pull Request у `main`. Виконуваний JAR зберігається як артефакт запуску.

Виконуваний файл: `target/katalog-knyzhok-2.0.0.jar`.

## Важливо

Java `record` потребує JDK 16+, тому в проєкті встановлено цільову версію Java 17.
