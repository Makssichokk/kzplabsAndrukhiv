"""Перевірка wheel у чистому середовищі (без pytest і ruff)."""

import subprocess
import sys
import tempfile
import venv
from pathlib import Path

ROOT = Path(__file__).parents[1]


def run(*command: str | Path, check: bool = True) -> subprocess.CompletedProcess[str]:
    return subprocess.run(
        [str(part) for part in command],
        capture_output=True,
        text=True,
        check=check,
    )


def main() -> int:
    wheels = sorted((ROOT / "dist").glob("kzp_lab06-*.whl"))
    if not wheels:
        print("Немає wheel у dist/: спершу python -m build", file=sys.stderr)
        return 1
    with tempfile.TemporaryDirectory() as folder:
        env_dir = Path(folder) / "venv"
        venv.EnvBuilder(with_pip=True).create(env_dir)
        bin_dir = env_dir / ("Scripts" if sys.platform == "win32" else "bin")
        python = bin_dir / ("python.exe" if sys.platform == "win32" else "python")
        run(python, "-m", "pip", "install", "--no-deps", wheels[-1])
        if run(python, "-c", "import pytest", check=False).returncode == 0:
            print("pytest не має бути в чистому середовищі", file=sys.stderr)
            return 1
        out = Path(folder) / "summary.txt"
        run(python, "-m", "kzp_lab06.cli", ROOT / "data" / "input.csv", out)
        expected = (ROOT / "baseline" / "summary.txt").read_bytes()
        if out.read_bytes() != expected:
            print("Звіт не збігається з еталоном", file=sys.stderr)
            return 1
    print(f"OK: {wheels[-1].name} встановлено, звіт збігається з еталоном")
    return 0


if __name__ == "__main__":
    sys.exit(main())
