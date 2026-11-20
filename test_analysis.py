from kzp_lab07.analysis import make_summary, ranking_key
from kzp_lab07.loader import load_books


def test_summary():
    result = make_summary(load_books("data/input.csv"))
    assert result.count == 6
    assert result.total_pages == 1930
    assert result.longest_title == 'Quantum "Garden"'
    assert result.authors == ("Ann Lee", "Bob Stone", "Олена Коваль")
    assert result.top_author_count == 2


def test_duck_typing():
    class Duck:
        title, author, year, pages, genre = "X", "Y", 2024, 5, "science"
        def reading_time(self): return 9.0
    result = make_summary([Duck()])
    assert result.total_reading_hours == 9.0
    assert ranking_key(Duck())[0] == -9
