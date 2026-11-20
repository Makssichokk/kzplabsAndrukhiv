import copy
import random

import pytest

from kzp_lab06.analysis import (
    DEFAULT_MIN_YEAR,
    count_by_genre,
    longest_book,
    make_summary,
    select_recent,
    stream_summary,
    summarize,
    unique_authors,
)
from kzp_lab06.loader import iter_lines
from samples import SAMPLE_LINES


def rec(title, author, year, pages, genre):
    return {"title": title, "author": author, "year": year, "pages": pages,
            "genre": genre}


@pytest.fixture
def records():
    return list(iter_lines(SAMPLE_LINES))


def test_select_boundary_is_inclusive(records):
    selected = select_recent(records)
    assert [r["year"] for r in selected] == [2020, 2021, 2023, 2020]
    assert all(r["year"] >= DEFAULT_MIN_YEAR for r in selected)


def test_unique_authors_set(records):
    assert unique_authors(records) == {"Ann", "Bob"}


def test_count_by_genre_sorted_and_counts_duplicates(records):
    counts = count_by_genre(select_recent(records))
    assert counts == {"novel": 3, "poetry": 1}
    assert list(counts) == sorted(counts)


def test_longest_tie_is_deterministic():
    a = rec("B", "X", 2020, 100, "g")
    b = rec("A", "X", 2020, 100, "g")
    assert longest_book([a, b]) is b
    assert longest_book([b, a]) is b
    assert longest_book([]) is None


def test_summarize_defaults(records):
    s = summarize(records)
    assert s.count == 4
    assert s.totals == {} and s.labels == {}
    assert s.minimum_year == 2020


def test_summarize_metrics_and_labels(records):
    s = summarize(records, "year", "pages", pages="Сторінки")
    assert s.totals == {"year": 8084, "pages": 1500}
    assert s.labels == {"year": "year", "pages": "Сторінки"}


def test_summarize_unknown_metric(records):
    with pytest.raises(ValueError, match="Невідома метрика"):
        summarize(records, "price")


def test_summarize_empty_selection():
    s = summarize([rec("Old", "A", 1999, 10, "g")], "pages")
    assert s.count == 0 and s.totals == {"pages": 0}
    assert s.longest is None and s.authors == frozenset() and s.by_genre == {}


def test_minimum_year_default_is_not_shared_between_calls(records):
    assert summarize(records, minimum_year=2022).count == 1
    assert summarize(records).count == 4
    assert summarize(records, minimum_year=2019).count == 5


def test_summary_independent_of_row_order(records):
    expected = make_summary(records)
    rng = random.Random(7)
    for _ in range(10):
        shuffled = records[:]
        rng.shuffle(shuffled)
        assert make_summary(shuffled) == expected


def test_stream_equals_list_summary(records):
    assert stream_summary(iter(records)) == make_summary(records)
    assert stream_summary(iter(records), 2022) == make_summary(records, 2022)


def test_stream_accepts_generator_and_empty():
    assert stream_summary(r for r in []).count == 0


def test_records_are_not_modified(records):
    before = copy.deepcopy(records)
    make_summary(records)
    stream_summary(iter(records))
    assert records == before
