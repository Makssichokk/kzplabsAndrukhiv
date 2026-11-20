"""Поліморфний аналіз без перевірок конкретних класів."""
from collections import Counter
from collections.abc import Sequence

from .models import BookSummary, GenreCount, Readable


def ranking_key(book: Readable) -> tuple[float, str, str]:
    return (-book.reading_time(), book.title, book.author)


def make_summary(books: Sequence[Readable], minimum_year: int = 2020) -> BookSummary:
    selected = [book for book in books if book.year >= minimum_year]
    genres = Counter(book.genre for book in selected)
    authors = Counter(book.author for book in selected)
    longest = min(selected, key=lambda b: (-b.pages, b.title, b.author, b.year, b.genre), default=None)
    top_author = min(authors, key=lambda author: (-authors[author], author)) if authors else None
    return BookSummary(
        count=len(selected),
        total_pages=sum(book.pages for book in selected),
        longest_title=longest.title if longest else None,
        longest_author=longest.author if longest else None,
        longest_year=longest.year if longest else None,
        longest_pages=longest.pages if longest else 0,
        genres=tuple(GenreCount(g, n) for g, n in sorted(genres.items())),
        authors=tuple(sorted(authors)),
        top_author=top_author,
        top_author_count=authors[top_author] if top_author else 0,
        total_reading_hours=sum(book.reading_time() for book in selected),
    )
