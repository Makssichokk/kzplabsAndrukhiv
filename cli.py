"""Командний інтерфейс бібліотеки."""
import argparse
import sys

from .analysis import make_summary
from .errors import DataError
from .loader import load_books
from .reporting import format_report, write_report


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description="Лабораторна №7 — бібліотека")
    parser.add_argument("input", help="Вхідний CSV")
    parser.add_argument("output", help="Вихідний TXT")
    parser.add_argument("--minimum-year", type=int, default=2020)
    parser.add_argument("--times", action="store_true", help="Додати поліморфний час і топ автора")
    args = parser.parse_args(argv)
    try:
        summary = make_summary(load_books(args.input), args.minimum_year)
        write_report(args.output, format_report(summary, args.minimum_year, times=args.times))
    except DataError as error:
        print(f"Помилка даних: {error}", file=sys.stderr)
        return 1
    except OSError as error:
        print(f"Помилка файла: {error}", file=sys.stderr)
        return 3
    except Exception as error:
        print(f"Неочікувана помилка: {error}", file=sys.stderr)
        return 4
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
