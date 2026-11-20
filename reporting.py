"""Стабільний текстовий звіт і атомарний запис через with."""
from pathlib import Path
from typing import TextIO

from .models import BookSummary


def format_report(summary: BookSummary, minimum_year: int = 2020, *, times: bool = False) -> str:
    lines = [
        f"Звіт: бібліотека, книги з року {minimum_year} і пізніше",
        f"Кількість книг: {summary.count}",
        f"Сумарна кількість сторінок: {summary.total_pages}",
    ]
    if summary.longest_title is None:
        lines.append("Найдовша книга: немає")
    else:
        lines.append(f"Найдовша книга: {summary.longest_title} ({summary.longest_author}, {summary.longest_year}), {summary.longest_pages} стор.")
    lines.append("Кількість книг за жанрами:")
    lines.extend(f"{item.genre}: {item.count}" for item in summary.genres)
    lines.append(f"Автори ({len(summary.authors)}):")
    lines.extend(summary.authors)
    if times:
        lines.extend(("", "Час читання/прослуховування:", f"Загалом: {summary.total_reading_hours:.2f} год.",
                      f"Топ автора: {summary.top_author or 'немає'} ({summary.top_author_count} книг)"))
    return "\n".join(lines) + "\n"


class ReportWriter:
    def __init__(self, path: str | Path):
        self.path = Path(path)
        self.tmp = self.path.with_name(self.path.name + ".tmp")
        self.file: TextIO | None = None

    @property
    def closed(self) -> bool:
        return self.file is None or self.file.closed

    def __enter__(self) -> "ReportWriter":
        self.path.parent.mkdir(parents=True, exist_ok=True)
        self.file = self.tmp.open("w", encoding="utf-8", newline="\n")
        return self

    def write(self, text: str) -> None:
        if self.file is None or self.file.closed:
            raise ValueError("Файл звіту закритий")
        self.file.write(text)

    def __exit__(self, exc_type: object, exc: object, tb: object) -> bool:
        if self.file is not None:
            self.file.close()
        if exc_type is None:
            self.tmp.replace(self.path)
        else:
            self.tmp.unlink(missing_ok=True)
        return False


def write_report(path: str | Path, text: str) -> None:
    with ReportWriter(path) as writer:
        writer.write(text)
