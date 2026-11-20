# Звіт: лабораторна №7, варіант 1 — Бібліотека

Модель: `Book` (ABC), `PrintedBook`, `AudioBook`, `Readable` (Protocol),
`GenreCount` і `BookSummary` (`@dataclass(frozen=True)`).

CSV: `title,author,year,pages,genre`, без додаткових стовпців.
Основний звіт має збігатися з `baseline/summary.txt`.
Додатковий режим `--times` показує поліморфний час і топ автора.

**Підтверджуйте результати після запуску:** `python -m pytest`,
`ruff check .`, `ruff format --check .`, `python -m build`,
`python scripts/verify_wheel.py` та GitHub Actions.

**Примітка:** умовні швидкості читання — навчальне припущення, оскільки
CSV не містить тривалості аудіокниг. Дані `data/input.csv` — відтворений
приклад для еталонного звіту; перед здачею порівняйте з власним CSV №6.

Розробка з допомогою ChatGPT (OpenAI).
