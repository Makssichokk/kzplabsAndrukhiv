import subprocess
import sys
from pathlib import Path
from unittest import mock

import pytest

from kzp_lab06.cli import main
from samples import HEADER, SAMPLE_CSV, SAMPLE_REPORT

ROOT = Path(__file__).parents[1]
ENV = {"PYTHONPATH": str(ROOT / "src")}


def make_input(tmp_path, text=SAMPLE_CSV):
    path = tmp_path / "in.csv"
    path.write_text(text, encoding="utf-8")
    return path


def test_success_report_and_input_untouched(tmp_path, capsys):
    src, dst = make_input(tmp_path), tmp_path / "out" / "report.txt"
    before = src.read_bytes()
    assert main([str(src), str(dst)]) == 0
    assert dst.read_text(encoding="utf-8") == SAMPLE_REPORT
    assert src.read_bytes() == before
    assert "Звіт записано" in capsys.readouterr().out


def test_stream_mode_gives_same_report(tmp_path):
    src = make_input(tmp_path)
    a, b = tmp_path / "a.txt", tmp_path / "b.txt"
    assert main([str(src), str(a)]) == 0
    assert main([str(src), str(b), "--stream"]) == 0
    assert a.read_bytes() == b.read_bytes()


def test_min_year_option(tmp_path):
    src, dst = make_input(tmp_path), tmp_path / "out.txt"
    assert main([str(src), str(dst), "--min-year", "2022"]) == 0
    assert "Кількість книг: 1" in dst.read_text(encoding="utf-8")


def test_header_only_input(tmp_path):
    src, dst = make_input(tmp_path, HEADER), tmp_path / "out.txt"
    assert main([str(src), str(dst)]) == 0
    assert "Кількість книг: 0" in dst.read_text(encoding="utf-8")


@pytest.mark.parametrize("flags", [[], ["--stream"]])
def test_data_error_code_1(tmp_path, capsys, flags):
    src = make_input(tmp_path, HEADER + "T,A,2020,x,g\n")
    dst = tmp_path / "out.txt"
    assert main([str(src), str(dst), *flags]) == 1
    err = capsys.readouterr().err
    assert "Помилка даних: Рядок 2: поле pages має бути цілим числом" in err
    assert "Причина:" in err
    assert not dst.exists()


def test_missing_file_code_3(tmp_path, capsys):
    assert main([str(tmp_path / "nope.csv"), str(tmp_path / "o.txt")]) == 3
    assert "Помилка файла" in capsys.readouterr().err


def test_no_arguments_code_2(capsys):
    with pytest.raises(SystemExit) as info:
        main([])
    assert info.value.code == 2
    assert "usage" in capsys.readouterr().err


def test_same_input_and_output_code_2(tmp_path):
    src = make_input(tmp_path)
    before = src.read_bytes()
    with pytest.raises(SystemExit) as info:
        main([str(src), str(src)])
    assert info.value.code == 2
    assert src.read_bytes() == before


def test_unexpected_error_code_4(tmp_path, capsys):
    src, dst = make_input(tmp_path), tmp_path / "out.txt"
    with mock.patch("kzp_lab06.cli.make_summary", side_effect=RuntimeError("boom")):
        assert main([str(src), str(dst)]) == 4
    assert "Неочікувана помилка: boom" in capsys.readouterr().err


def test_matches_baseline(tmp_path):
    dst = tmp_path / "summary.txt"
    assert main([str(ROOT / "data" / "input.csv"), str(dst)]) == 0
    assert dst.read_bytes() == (ROOT / "baseline" / "summary.txt").read_bytes()


def test_run_as_module(tmp_path):
    src, dst = make_input(tmp_path), tmp_path / "out.txt"
    result = subprocess.run(
        [sys.executable, "-m", "kzp_lab06.cli", str(src), str(dst)],
        capture_output=True,
        env=ENV,
    )
    assert result.returncode == 0, result.stderr
    assert dst.read_text(encoding="utf-8") == SAMPLE_REPORT


def test_import_has_no_side_effects(tmp_path):
    code = (
        "import kzp_lab06, kzp_lab06.errors, kzp_lab06.loader, "
        "kzp_lab06.analysis, kzp_lab06.reporting, kzp_lab06.cli"
    )
    result = subprocess.run(
        [sys.executable, "-c", code],
        capture_output=True,
        env=ENV,
        cwd=tmp_path,
    )
    assert result.returncode == 0
    assert result.stdout == b"" and result.stderr == b""
    assert list(tmp_path.iterdir()) == []
