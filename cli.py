"""Інтерфейс командного рядка: аргументи, коди завершення, межа помилок.

Коди: 0 успіх, 1 DataError, 2 помилка аргументів (argparse), 3 помилка файла,
4 неочікувана помилка.
"""

import argparse
import sys
from collections.abc import Sequence
from pathlib import Path

from .analysis import DEFAULT_MIN_YEAR, make_summary, stream_summary
from .errors import DataError
from .loader import iter_records, load_records
from .reporting import write_report


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        prog="kzp-lab06",
        description="Звіт за CSV-файлом бібліотеки (книги з year >= 2020).",
    )
    parser.add_argument("input", type=Path, help="вхідний CSV-файл")
    parser.add_argument("output", type=Path, help="вихідний текстовий звіт")
    parser.add_argument(
        "--min-year",
        type=int,
        default=DEFAULT_MIN_YEAR,
        help=f"нижня межа року (типово {DEFAULT_MIN_YEAR}, межа входить)",
    )
    parser.add_argument(
        "--stream",
        action="store_true",
        help="потокова обробка без збереження записів у пам'яті",
    )
    return parser


def main(argv: Sequence[str] | None = None) -> int:
    parser = build_parser()
    args = parser.parse_args(argv)
    if args.input.resolve() == args.output.resolve():
        parser.error("вхідний і вихідний файли мають бути різними")
    try:
        if args.stream:
            summary = stream_summary(iter_records(args.input), args.min_year)
        else:
            summary = make_summary(load_records(args.input), args.min_year)
        write_report(summary, args.output)
    except DataError as error:
        print(f"Помилка даних: {error}", file=sys.stderr)
        if error.__cause__ is not None:
            print(f"Причина: {error.__cause__}", file=sys.stderr)
        return 1
    except OSError as error:
        print(f"Помилка файла: {error}", file=sys.stderr)
        return 3
    except Exception as error:  # межа CLI: помилка стає повідомленням і кодом 4
        print(f"Неочікувана помилка: {error}", file=sys.stderr)
        return 4
    print(f"Звіт записано: {args.output}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
