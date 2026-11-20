import random

from kzp_lab06.analysis import make_summary
from kzp_lab06.loader import iter_lines
from kzp_lab06.reporting import format_report, write_report
from samples import EMPTY_REPORT, HEADER, SAMPLE_LINES, SAMPLE_REPORT


def test_format_report_exact():
    summary = make_summary(list(iter_lines(SAMPLE_LINES)))
    assert format_report(summary) == SAMPLE_REPORT


def test_empty_report():
    assert format_report(make_summary(list(iter_lines([HEADER])))) == EMPTY_REPORT


def test_report_does_not_depend_on_row_order():
    records = list(iter_lines(SAMPLE_LINES))
    rng = random.Random(3)
    for _ in range(10):
        rng.shuffle(records)
        assert format_report(make_summary(records)) == SAMPLE_REPORT


def test_write_report_utf8_lf_and_directory(tmp_path):
    target = tmp_path / "nested" / "dir" / "report.txt"
    summary = make_summary(list(iter_lines(SAMPLE_LINES)))
    write_report(summary, target)
    data = target.read_bytes()
    assert data == SAMPLE_REPORT.encode("utf-8")
    assert b"\r" not in data


def test_write_report_overwrites(tmp_path):
    target = tmp_path / "report.txt"
    target.write_text("old content that is longer than the new one" * 50)
    write_report(make_summary([]), target)
    assert target.read_text(encoding="utf-8") == EMPTY_REPORT
