from kzp_lab07.cli import main


def test_success(tmp_path):
    path = tmp_path / "out.txt"
    assert main(["data/input.csv", str(path)]) == 0
    assert "Кількість книг: 6" in path.read_text(encoding="utf-8")


def test_missing_file(tmp_path):
    assert main([str(tmp_path / "missing.csv"), str(tmp_path / "out.txt")]) == 3
