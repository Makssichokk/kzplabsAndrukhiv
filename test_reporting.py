import pytest
from kzp_lab07.analysis import make_summary
from kzp_lab07.loader import load_books
from kzp_lab07.reporting import ReportWriter, format_report


def test_baseline():
    actual = format_report(make_summary(load_books("data/input.csv")))
    with open("baseline/summary.txt", encoding="utf-8") as f:
        assert actual == f.read()


def test_atomic(tmp_path):
    path = tmp_path / "report.txt"
    path.write_text("old", encoding="utf-8")
    with pytest.raises(RuntimeError):
        with ReportWriter(path) as writer:
            writer.write("new")
            raise RuntimeError("stop")
    assert path.read_text(encoding="utf-8") == "old"
    assert not (tmp_path / "report.txt.tmp").exists()
