import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PagePriceTest {
    @Test
    void recordStoresValidValues() {
        PagePrice value = new PagePrice(120, 99.90);
        assertEquals(120, value.pages());
        assertEquals(99.90, value.price(), 0.0001);
    }

    @Test
    void recordRejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> new PagePrice(0, 10));
        assertThrows(IllegalArgumentException.class, () -> new PagePrice(10, -1));
        assertThrows(IllegalArgumentException.class,
                () -> new PagePrice(10, Double.POSITIVE_INFINITY));
    }

    @Test
    void recordsWithSameValuesAreEqual() {
        assertEquals(new PagePrice(100, 10.5), new PagePrice(100, 10.5));
        assertEquals(new PagePrice(100, 10.5).hashCode(), new PagePrice(100, 10.5).hashCode());
    }

    @Test
    void recordAcceptsZeroPrice() {
        assertEquals(0.0, new PagePrice(1, 0).price(), 0.0001);
    }

    @Test
    void recordRejectsNaN() {
        assertThrows(IllegalArgumentException.class, () -> new PagePrice(10, Double.NaN));
    }
}
