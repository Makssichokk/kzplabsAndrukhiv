"""Читання CSV (модуль csv) і перетворення рядків на записи зі типами."""

import csv
import re
from collections.abc import Iterable, Iterator
from pathlib import Path
from typing import Any

from .errors import DataError

FIELDS = ("title", "author", "year", "pages", "genre")
INT_FIELDS = ("year", "pages")
_INT_RE = re.compile(r"[+-]?[0-9]+")


def normalize_text(text: str) -> str:
    """Прибрати зайві пробіли: " ".join(text.split())."""
    return " ".join(text.split())


def _parse_text(raw: object, field: str, line: int) -> str:
    text = normalize_text(raw) if isinstance(raw, str) else ""
    if not text:
        raise DataError(f"Рядок {line}: поле {field} порожнє", line=line, field=field)
    return text


def _parse_int(raw: object, field: str, line: int) -> int:
    text = raw.strip() if isinstance(raw, str) else ""
    if not text:
        raise DataError(f"Рядок {line}: поле {field} порожнє", line=line, field=field)
    try:
        if not _INT_RE.fullmatch(text):
            raise ValueError(text)
        value = int(text)
    except ValueError as error:
        raise DataError(
            f"Рядок {line}: поле {field} має бути цілим числом",
            line=line,
            field=field,
        ) from error
    if value < 0:
        raise DataError(
            f"Рядок {line}: поле {field} має бути не менше 0",
            line=line,
            field=field,
        )
    return value


def parse_row(row: dict[Any, Any], line: int) -> dict[str, Any]:
    """Перетворити один рядок DictReader на запис; line — номер у файлі."""
    if None in row:
        raise DataError(f"Рядок {line}: забагато полів", line=line)
    record: dict[str, Any] = {}
    for field in FIELDS:
        if field in INT_FIELDS:
            record[field] = _parse_int(row.get(field), field, line)
        else:
            record[field] = _parse_text(row.get(field), field, line)
    return record


def iter_lines(lines: Iterable[str]) -> Iterator[dict[str, Any]]:
    """Генератор записів з рядків CSV (ледачий: помилки виникають під час обходу)."""
    reader = csv.DictReader(lines)
    header = reader.fieldnames
    if not header:
        raise DataError("Порожній CSV: немає заголовка", line=1)
    names = [name.strip() for name in header]
    reader.fieldnames = names
    missing = [field for field in FIELDS if field not in names]
    if missing:
        joined = ", ".join(missing)
        raise DataError(f"Відсутні стовпці: {joined}", line=1, field=joined)
    for row in reader:
        yield parse_row(row, reader.line_num)


def iter_records(path: str | Path) -> Iterator[dict[str, Any]]:
    """Генератор записів файла; файл відкривається лише на читання."""
    try:
        with open(path, newline="", encoding="utf-8-sig") as handle:
            yield from iter_lines(handle)
    except UnicodeDecodeError as error:
        raise DataError("Файл не є коректним UTF-8") from error


def load_records(path: str | Path) -> list[dict[str, Any]]:
    """Прочитати всі записи у список словників."""
    return list(iter_records(path))
