# kzp-lab06 (варіант 1: Бібліотека)

CSV `title,author,year,pages,genre` → стабільний текстовий звіт.
Відбір `year >= 2020`; множина — автори; словник — кількість книг за `genre`;
звіт — кількість, сумарні сторінки, найдовша книга.

    pip install -e ".[dev]"
    kzp-lab06 data/input.csv report.txt          # або python -m kzp_lab06.cli ...
    kzp-lab06 data/input.csv report.txt --stream # потокова обробка

Коди завершення: 0, 1 (DataError), 2 (аргументи), 3 (файл), 4 (неочікувана).

Перевірка: `python -m pytest`, `ruff check .`, `ruff format --check .`,
`python -m build`, `python scripts/verify_wheel.py`,
`python scripts/measure_memory.py`.
