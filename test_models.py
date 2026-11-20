from dataclasses import FrozenInstanceError
import pytest
from kzp_lab07.models import AudioBook, Book, PrintedBook, GenreCount


def test_polymorphism():
    a = PrintedBook("A", "B", 2023, 240, "novel")
    b = AudioBook("A", "B", 2023, 240, "audio")
    assert a.reading_time() == 6
    assert b.reading_time() == 4
    assert a != b
    assert "PrintedBook" in repr(a)
    assert "A (B" in str(a)
    with pytest.raises(TypeError):
        hash(a)
    with pytest.raises(TypeError):
        Book("A", "B", 2023, 240, "novel")


@pytest.mark.parametrize("value", [-1, True, 1.5])
def test_invalid_pages(value):
    book = PrintedBook("A", "B", 2023, 20, "novel")
    with pytest.raises(ValueError):
        book.pages = value
    assert book.pages == 20


def test_dataclass_frozen():
    item = GenreCount("novel", 3)
    with pytest.raises(FrozenInstanceError):
        item.count = 5
    assert {item: 1}[item] == 1
