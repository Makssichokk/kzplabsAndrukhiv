"""Відбір, множина авторів, словник за жанрами та підсумки. Файли не відкриває."""

from collections.abc import Iterable
from dataclasses import dataclass
from typing import Any

Record = dict[str, Any]

DEFAULT_MIN_YEAR = 2020
METRICS = ("year", "pages")
PAGES_LABEL = "Сумарна кількість сторінок"


@dataclass(frozen=True)
class Summary:
    """Підсумок обчислень (порівнюється за значенням)."""

    minimum_year: int
    count: int
    authors: frozenset[str]
    by_genre: dict[str, int]
    longest: Record | None
    totals: dict[str, int]
    labels: dict[str, str]


def select_recent(
    records: Iterable[Record], minimum_year: int = DEFAULT_MIN_YEAR
) -> list[Record]:
    """Відбір: year >= minimum_year (межа входить), порядок зберігається."""
    return [r for r in records if r["year"] >= minimum_year]


def unique_authors(records: Iterable[Record]) -> set[str]:
    """Множина унікальних авторів."""
    return {str(r["author"]) for r in records}


def count_by_genre(records: list[Record]) -> dict[str, int]:
    """Словник «жанр -> кількість книг», ключі впорядковано."""
    genres = {str(r["genre"]) for r in records}
    return {g: sum(1 for r in records if r["genre"] == g) for g in sorted(genres)}


def _longest_key(record: Record) -> tuple[Any, ...]:
    """Ключ: більше сторінок краще; за рівності — однозначний вибір за текстом."""
    return (
        -record["pages"],
        record["title"],
        record["author"],
        record["year"],
        record["genre"],
    )


def longest_book(records: Iterable[Record]) -> Record | None:
    """Найдовша книга або None для порожнього набору."""
    return min(records, key=_longest_key, default=None)


def summarize(
    records: Iterable[Record],
    *metrics: str,
    minimum_year: int = DEFAULT_MIN_YEAR,
    **labels: str,
) -> Summary:
    """Підсумок за списком записів.

    minimum_year — типове значення незмінне, стану між викликами немає;
    *metrics — назви числових полів (year, pages), для яких рахується сума;
    **labels — підписи метрик (metric="Підпис"), типово — назва метрики.
    Невідома метрика дає ValueError; порожній відбір дає суми 0.
    """
    unknown = [m for m in metrics if m not in METRICS]
    if unknown:
        raise ValueError(f"Невідома метрика: {', '.join(unknown)}")
    selected = select_recent(records, minimum_year)
    return Summary(
        minimum_year=minimum_year,
        count=len(selected),
        authors=frozenset(unique_authors(selected)),
        by_genre=count_by_genre(selected),
        longest=longest_book(selected),
        totals={m: sum(r[m] for r in selected) for m in metrics},
        labels={m: labels.get(m, m) for m in metrics},
    )


def make_summary(
    records: Iterable[Record], minimum_year: int = DEFAULT_MIN_YEAR
) -> Summary:
    """Підсумок за вимогами варіанта: кількість, сторінки, найдовша книга."""
    return summarize(records, "pages", minimum_year=minimum_year, pages=PAGES_LABEL)


def stream_summary(
    records: Iterable[Record], minimum_year: int = DEFAULT_MIN_YEAR
) -> Summary:
    """Той самий підсумок за один прохід без збереження записів."""
    count = 0
    total_pages = 0
    authors: set[str] = set()
    genres: dict[str, int] = {}
    best: Record | None = None
    for record in records:
        if record["year"] < minimum_year:
            continue
        count += 1
        total_pages += record["pages"]
        authors.add(str(record["author"]))
        genres[record["genre"]] = genres.get(record["genre"], 0) + 1
        if best is None or _longest_key(record) < _longest_key(best):
            best = record
    return Summary(
        minimum_year=minimum_year,
        count=count,
        authors=frozenset(authors),
        by_genre={g: genres[g] for g in sorted(genres)},
        longest=best,
        totals={"pages": total_pages},
        labels={"pages": PAGES_LABEL},
    )
