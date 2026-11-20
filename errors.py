"""Винятки пакета."""


class DataError(ValueError):
    """Помилка вхідних даних; повідомлення вже містить рядок і поле."""

    def __init__(
        self,
        message: str,
        *,
        line: int | None = None,
        field: str | None = None,
    ) -> None:
        super().__init__(message)
        self.line = line
        self.field = field
