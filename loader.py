"""CSV -> об'єкти; початкові п'ять колонок не змінюються."""
import csv
from pathlib import Path

from .errors import DataError
from .models import AudioBook, Book, PrintedBook

FIELDS = ("title", "author", "year", "pages", "genre")


def build_book(row: dict[str, str | None], line: int) -> Book:
    if None in row:
        raise DataError(f"Рядок {line}: забагато полів")
    values: dict[str, str | int] = {}
    for key in FIELDS:
        raw = row.get(key)
        if raw is None or not raw.strip():
            raise DataError(f"Рядок {line}: поле {key} порожнє")
        if key in ("year", "pages"):
            try:
                # int('1_000') дозволений у Python, але не у строгому CSV.
                if not raw.strip().isascii() or not raw.strip().isdecimal():
                    raise ValueError(raw)
                values[key] = int(raw.strip())
            except ValueError as error:
                raise DataError(f"Рядок {line}: поле {key} має бути цілим числом") from error
        else:
            values[key] = raw
    # Без нової колонки тип визначається домовленістю: жанр audio/audiobook.
    cls = AudioBook if str(values["genre"]).strip().lower() in {"audio", "audiobook"} else PrintedBook
    try:
        return cls(**values)
    except ValueError as error:
        raise DataError(f"Рядок {line}: {error}") from error


def load_books(path: str | Path) -> list[Book]:
    try:
        with Path(path).open("r", encoding="utf-8-sig", newline="") as file:
            reader = csv.DictReader(file)
            if reader.fieldnames is None:
                raise DataError("Порожній CSV: немає заголовка")
            missing = [key for key in FIELDS if key not in reader.fieldnames]
            if missing:
                raise DataError("Відсутні стовпці: " + ", ".join(missing))
            return [build_book(row, reader.line_num) for row in reader]
    except UnicodeError as error:
        raise DataError("Файл не є коректним UTF-8") from error
    except csv.Error as error:
        raise DataError("Пошкоджений CSV") from error
