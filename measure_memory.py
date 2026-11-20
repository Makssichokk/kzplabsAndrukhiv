"""Порівняння списку записів і потокової обробки (tracemalloc)."""

import argparse
import tempfile
import time
import tracemalloc
from collections.abc import Callable
from pathlib import Path
from typing import Any

from kzp_lab06.analysis import make_summary, stream_summary
from kzp_lab06.loader import iter_records, load_records
from kzp_lab06.reporting import format_report


def make_csv(path: Path, rows: int) -> None:
    with path.open("w", encoding="utf-8", newline="\n") as handle:
        handle.write("title,author,year,pages,genre\n")
        for i in range(rows):
            handle.write(
                f"Book {i},Author {i % 500},{2000 + i % 30},"
                f"{100 + i % 900},genre{i % 12}\n"
            )


def measure(func: Callable[[], Any]) -> tuple[Any, float, float]:
    tracemalloc.start()
    started = time.perf_counter()
    result = func()
    elapsed = time.perf_counter() - started
    _, peak = tracemalloc.get_traced_memory()
    tracemalloc.stop()
    return result, peak / 2**20, elapsed


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--rows", type=int, default=200_000)
    rows = parser.parse_args().rows
    with tempfile.TemporaryDirectory() as folder:
        path = Path(folder) / "big.csv"
        make_csv(path, rows)
        listed, list_mib, list_time = measure(lambda: make_summary(load_records(path)))
        streamed, stream_mib, stream_time = measure(
            lambda: stream_summary(iter_records(path))
        )
    print(f"{'Режим':<44}{'Пік, МіБ':>10}{'Час, с':>9}")
    print(f"{'Список (load_records + make_summary)':<44}{list_mib:>10.2f}"
          f"{list_time:>9.2f}")
    print(f"{'Потоковий (iter_records + stream_summary)':<44}{stream_mib:>10.2f}"
          f"{stream_time:>9.2f}")
    print("Звіти однакові:", format_report(listed) == format_report(streamed))


if __name__ == "__main__":
    main()
