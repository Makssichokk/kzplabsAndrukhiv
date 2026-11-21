import java.util.List;
/** Контрольний набір із шести бібліотечних записів. */
public final class SampleBooks {
    private SampleBooks(){}
    /** Повертає зразки з комою, лапками та переносом рядка. @return записи */
    public static List<BookRecord> records(){return List.of(
        new BookRecord("Місто, яке читає","Олена Коваль",2022,320,"Роман"),
        new BookRecord("Java: практикум","Іван Петренко",2024,480,"Освіта"),
        new BookRecord("Книга \"Мрія\"","Марія Сидоренко",2021,210,"Фантастика"),
        new BookRecord("Дорога\nдодому","Андрій Бондар",2020,175,"Роман"),
        new BookRecord("Алгоритми","Тарас Мельник",2019,640,"Освіта"),
        new BookRecord("Весняний сад","Ірина Савчук",2023,128,"Поезія")
    );}
}
