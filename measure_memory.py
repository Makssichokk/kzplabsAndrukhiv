"""Орієнтовне порівняння словників та об'єктів через tracemalloc."""
import time
import tracemalloc
from kzp_lab07.models import PrintedBook


def measure(factory, count=100_000):
    tracemalloc.start()
    start = time.perf_counter()
    data = [factory(i) for i in range(count)]
    elapsed = time.perf_counter() - start
    _, peak = tracemalloc.get_traced_memory()
    print(f"{factory.__name__}: {peak / 1024**2:.2f} MiB, {elapsed:.2f} s, {len(data)} записів")
    tracemalloc.stop()


def dictionary(i):
    return {"title": f"Book {i}", "author": "Author", "year": 2024, "pages": 200, "genre": "novel"}


def object_model(i):
    return PrintedBook(f"Book {i}", "Author", 2024, 200, "novel")


if __name__ == "__main__":
    measure(dictionary)
    measure(object_model)
