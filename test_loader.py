from itertools import islice

import pytest

from kzp_lab06.errors import DataError
from kzp_lab06.loader import (
    iter_lines,
    iter_records,
    load_records,
    normalize_text,
    parse_row,
)
from samples import HEADER, SAMPLE_CSV, SAMPLE_LINES


def parse(*rows):
    return list(iter_lines([HEADER, *rows]))


def test_parse_row_types():
    row = {"title": "T", "author": "A", "year": "2021", "pages": "10", "genre": "g"}
    record = parse_row(row, 2)
    assert record == {"title": "T", "author": "A", "year": 2021, "pages": 10,
                      "genre": "g"}
    assert isinstance(record["year"], int) and isinstance(record["pages"], int)


def test_normalize_text():
    assert normalize_text("  a   b \t c ") == "a b c"


def test_whitespace_is_normalized_in_text_fields():
    record = parse("  Big   Book ,  Ann   Lee ,2020,5,  sci   fi \n")[0]
    assert (record["title"], record["author"], record["genre"]) == (
        "Big Book",
        "Ann Lee",
        "sci fi",
    )


def test_quoted_comma_and_quotes():
    records = list(iter_lines(SAMPLE_LINES))
    assert records[3]["title"] == 'C, the "Book"'


def test_duplicates_are_kept():
    records = list(iter_lines(SAMPLE_LINES))
    assert len(records) == 5
    assert records[1] == records[4]


def test_crlf_and_bom(tmp_path):
    path = tmp_path / "in.csv"
    path.write_bytes(b"\xef\xbb\xbf" + SAMPLE_CSV.replace("\n", "\r\n").encode())
    assert len(load_records(path)) == 5


def test_header_only_gives_empty_list(tmp_path):
    path = tmp_path / "in.csv"
    path.write_text(HEADER, encoding="utf-8")
    assert load_records(path) == []


@pytest.mark.parametrize("content", ["", "\n", "\n\n"])
def test_no_header(tmp_path, content):
    path = tmp_path / "in.csv"
    path.write_text(content, encoding="utf-8")
    with pytest.raises(DataError) as info:
        load_records(path)
    assert str(info.value) == "Порожній CSV: немає заголовка"


def test_missing_columns():
    with pytest.raises(DataError) as info:
        list(iter_lines(["title,author,year\n"]))
    assert str(info.value) == "Відсутні стовпці: pages, genre"


@pytest.mark.parametrize(
    ("row", "message", "field"),
    [
        ("T,A,abc,10,g", "Рядок 2: поле year має бути цілим числом", "year"),
        ("T,A,2020,12.5,g", "Рядок 2: поле pages має бути цілим числом", "pages"),
        ("T,A,2020,1_000,g", "Рядок 2: поле pages має бути цілим числом", "pages"),
        ("T,A,-1,10,g", "Рядок 2: поле year має бути не менше 0", "year"),
        ("T,A,2020,-5,g", "Рядок 2: поле pages має бути не менше 0", "pages"),
        ("T,A,2020,,g", "Рядок 2: поле pages порожнє", "pages"),
        ("T,A,2020,5", "Рядок 2: поле genre порожнє", "genre"),
        ("T,,2020,5,g", "Рядок 2: поле author порожнє", "author"),
        ("   ,A,2020,5,g", "Рядок 2: поле title порожнє", "title"),
        ("T,A,2020,5,g,extra", "Рядок 2: забагато полів", None),
    ],
)
def test_row_errors(row, message, field):
    with pytest.raises(DataError) as info:
        parse(row + "\n")
    assert str(info.value) == message
    assert info.value.line == 2
    assert info.value.field == field


def test_number_error_keeps_cause():
    with pytest.raises(DataError) as info:
        parse("T,A,x,5,g\n")
    assert isinstance(info.value.__cause__, ValueError)


def test_line_number_counts_physical_lines():
    text = HEADER + '"Two\nlines",A,2020,5,g\n' + "T,A,2020,oops,g\n"
    with pytest.raises(DataError) as info:
        list(iter_lines(text.splitlines(keepends=True)))
    # заголовок — рядок 1, багаторядковий запис — 2-3, хибний запис — 4
    assert str(info.value) == "Рядок 4: поле pages має бути цілим числом"


def test_invalid_utf8(tmp_path):
    path = tmp_path / "in.csv"
    path.write_bytes(HEADER.encode() + b"\xff\xfe,A,2020,5,g\n")
    with pytest.raises(DataError) as info:
        load_records(path)
    assert isinstance(info.value.__cause__, UnicodeDecodeError)


def test_iter_records_is_lazy(tmp_path):
    path = tmp_path / "in.csv"
    path.write_text(HEADER + "A,B,2020,1,g\nC,D,2021,2,g\nbad,row\n", encoding="utf-8")
    first_two = list(islice(iter_records(path), 2))
    assert [r["title"] for r in first_two] == ["A", "C"]
    with pytest.raises(DataError):
        load_records(path)


def test_input_is_not_modified(tmp_path):
    path = tmp_path / "in.csv"
    path.write_text(SAMPLE_CSV, encoding="utf-8")
    before = path.read_bytes()
    load_records(path)
    assert path.read_bytes() == before
