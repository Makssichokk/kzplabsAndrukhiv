import pytest
from kzp_lab07.loader import load_books
from kzp_lab07.errors import DataError
from kzp_lab07.models import AudioBook, PrintedBook


def test_load_sample():
    books = load_books("data/input.csv")
    assert len(books) == 8
    assert books[0].title == 'Quantum "Garden"'
    assert isinstance(books[0], PrintedBook)


def test_audio_convention():
    books = load_books("data/audio_example.csv")
    assert isinstance(books[0], AudioBook)
    assert isinstance(books[1], PrintedBook)


def test_bad_csv(tmp_path):
    path = tmp_path / "bad.csv"
    path.write_text("title,author,year,pages,genre\nA,B,2024,abc,novel\n", encoding="utf-8")
    with pytest.raises(DataError, match="Рядок 2"):
        load_books(path)


def test_empty_with_header(tmp_path):
    path = tmp_path / "empty.csv"
    path.write_text("title,author,year,pages,genre\n", encoding="utf-8")
    assert load_books(path) == []
