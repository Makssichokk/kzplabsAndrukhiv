"""Форматування та запис звіту. CSV не читає."""

from pathlib import Path

from .analysis import Summary


def format_report(summary: Summary) -> str:
    """Стабільний текст звіту: без дат і випадковості, групи й автори відсортовані."""
    lines = [
        f"Звіт: бібліотека, книги з року {summary.minimum_year} і пізніше",
        f"Кількість книг: {summary.count}",
    ]
    lines.extend(
        [f"{summary.labels[m]}: {total}" for m, total in summary.totals.items()]
    )
    book = summary.longest
    if book is None:
        lines.append("Найдовша книга: немає")
    else:
        lines.append(
            f"Найдовша книга: {book['title']} "
            f"({book['author']}, {book['year']}), {book['pages']} стор."
        )
    lines.append("Кількість книг за жанрами:")
    lines.extend([f"  {genre}: {n}" for genre, n in summary.by_genre.items()])
    lines.append(f"Автори ({len(summary.authors)}):")
    lines.extend([f"  {author}" for author in sorted(summary.authors)])
    return "\n".join(lines) + "\n"


def write_report(summary: Summary, path: str | Path) -> None:
    """Записати звіт у UTF-8 з \\n; каталог створюється за потреби."""
    target = Path(path)
    target.parent.mkdir(parents=True, exist_ok=True)
    with target.open("w", encoding="utf-8", newline="\n") as handle:
        handle.write(format_report(summary))
