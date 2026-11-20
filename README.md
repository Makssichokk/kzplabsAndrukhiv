# Лабораторна робота №7 — Бібліотека (варіант 1)

**НУ «Львівська політехніка», КІ-302, Андрухів М.С.**  
Python >= 3.11, macOS / Linux / Windows.

## Призначення

Переробка лабораторної №6 зі словників на об'єкти: абстрактний `Book`, підтипи
`PrintedBook` і `AudioBook`, поліморфний `reading_time()`, `Protocol`, незмінний
`BookSummary`, валідація `@property`, атомарний запис звіту.

## Встановлення та запуск

```bash
python3 -m venv .venv
source .venv/bin/activate
python -m pip install -e ".[dev]"
python -m kzp_lab07 data/input.csv report.txt
python -m kzp_lab07 data/audio_example.csv audio_report.txt --times
python -m pytest
ruff check .
ruff format --check .
python -m build
python scripts/verify_wheel.py
```

У Windows активація: `.venv\Scripts\activate`.

## Важливе про CSV

Формат лабораторної №6 **не змінено**: `title,author,year,pages,genre`.
У CSV немає типу книги й реальної тривалості аудіокниги. Щоб продемонструвати
обидва підтипи без додавання стовпців, застосовано **навчальну домовленість**:
жанр `audio` або `audiobook` означає `AudioBook`, усі інші — `PrintedBook`.
`reading_time()` використовує умовні швидкості 40 і 60 стор./год відповідно.
Це оцінка, а не фактична тривалість аудіозапису. Приклад у `data/audio_example.csv`.

Звичайний звіт не містить додаткових блоків і відповідає `baseline/summary.txt`.
`--times` додає час та топ автора. У звіті враховуються книги з 2020 року.

## Структура

- `src/kzp_lab07/models.py` — класи, Protocol, dataclass
- `loader.py` — CSV, валідація та вибір підтипу
- `analysis.py` — підсумок через спільний інтерфейс
- `reporting.py` — текст і `ReportWriter`
- `cli.py` — коди 0/1/2/3/4
- `tests/` — тести, зокрема порівняння з baseline
- `.github/workflows/ci.yml` — тести, ruff, wheel на 3 ОС

## GitHub

Створіть гілку `lab07`, завантажте **вміст цієї папки** (не ZIP), створіть Pull Request до `main`.
Не завантажуйте `.venv`, `dist`, `build` та кеші.

## Академічна доброчесність

Проєкт підготовлено за допомогою ChatGPT (OpenAI). Перед здачею перевірте код,
тести, збіг із фактичними даними лабораторної №6 та результати GitHub Actions.
