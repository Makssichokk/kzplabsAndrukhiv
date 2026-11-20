"""Перевіряє встановлений wheel у чистому venv."""
import os
from pathlib import Path
import subprocess
import tempfile
import venv

root = Path(__file__).resolve().parents[1]
wheels = sorted((root / "dist").glob("*.whl"))
if not wheels:
    raise SystemExit("Спочатку виконайте python -m build")
with tempfile.TemporaryDirectory() as directory:
    env = Path(directory) / "venv"
    venv.EnvBuilder(with_pip=True).create(env)
    python = env / ("Scripts/python.exe" if os.name == "nt" else "bin/python")
    subprocess.run([str(python), "-m", "pip", "install", "--no-deps", str(wheels[-1])], check=True)
    output = Path(directory) / "report.txt"
    subprocess.run([str(python), "-m", "kzp_lab07", str(root / "data/input.csv"), str(output)], check=True)
    assert output.read_bytes() == (root / "baseline/summary.txt").read_bytes()
print("Wheel OK: звіт збігається з еталоном")
