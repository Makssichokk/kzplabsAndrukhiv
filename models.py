"""Предметна модель книг і незмінні підсумки."""
from abc import ABC, abstractmethod
from dataclasses import dataclass
from typing import Protocol


class Readable(Protocol):
    @property
    def title(self) -> str: ...
    @property
    def author(self) -> str: ...
    @property
    def year(self) -> int: ...
    @property
    def pages(self) -> int: ...
    @property
    def genre(self) -> str: ...
    def reading_time(self) -> float: ...


class Book(ABC):
    """Абстрактна книга з перевіркою полів у властивостях."""
    __hash__ = None

    def __init__(self, title: str, author: str, year: int, pages: int, genre: str):
        self.title = title
        self.author = author
        self.year = year
        self.pages = pages
        self.genre = genre

    @staticmethod
    def _text(value: str, field: str) -> str:
        if not isinstance(value, str) or not value.strip():
            raise ValueError(f"поле {field} порожнє")
        return " ".join(value.split())

    @property
    def title(self) -> str: return self._title
    @title.setter
    def title(self, value: str) -> None: self._title = self._text(value, "title")

    @property
    def author(self) -> str: return self._author
    @author.setter
    def author(self, value: str) -> None: self._author = self._text(value, "author")

    @property
    def genre(self) -> str: return self._genre
    @genre.setter
    def genre(self, value: str) -> None: self._genre = self._text(value, "genre")

    @property
    def year(self) -> int: return self._year
    @year.setter
    def year(self, value: int) -> None:
        if type(value) is not int or value < 0:
            raise ValueError("поле year має бути цілим числом не менше 0")
        self._year = value

    @property
    def pages(self) -> int: return self._pages
    @pages.setter
    def pages(self, value: int) -> None:
        if type(value) is not int or value < 0:
            raise ValueError("поле pages має бути цілим числом не менше 0")
        self._pages = value

    @abstractmethod
    def reading_time(self) -> float:
        """Орієнтовна тривалість у годинах."""

    def __str__(self) -> str:
        return f"{self.title} ({self.author}, {self.year}), {self.pages} стор."

    def __repr__(self) -> str:
        return (f"{type(self).__name__}(title={self.title!r}, author={self.author!r}, "
                f"year={self.year!r}, pages={self.pages!r}, genre={self.genre!r})")

    def __eq__(self, other: object) -> bool:
        if not isinstance(other, Book):
            return NotImplemented
        return (type(self) is type(other) and
                (self.title, self.author, self.year, self.pages, self.genre) ==
                (other.title, other.author, other.year, other.pages, other.genre))


class PrintedBook(Book):
    """Паперова книга: 40 сторінок на годину (навчальне припущення)."""
    def reading_time(self) -> float:
        return self.pages / 40.0


class AudioBook(Book):
    """Аудіокнига: 60 умовних сторінок на годину (навчальне припущення)."""
    def reading_time(self) -> float:
        return self.pages / 60.0


@dataclass(frozen=True)
class GenreCount:
    genre: str
    count: int


@dataclass(frozen=True)
class BookSummary:
    count: int
    total_pages: int
    longest_title: str | None
    longest_author: str | None
    longest_year: int | None
    longest_pages: int
    genres: tuple[GenreCount, ...]
    authors: tuple[str, ...]
    top_author: str | None
    top_author_count: int
    total_reading_hours: float
