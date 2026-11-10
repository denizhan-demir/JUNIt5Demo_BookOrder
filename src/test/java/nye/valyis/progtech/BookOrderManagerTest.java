package nye.valyis.progtech;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// ==========================================
// 3. JUNIT 5 TEST CLASS: BookOrderManagerTest
// ==========================================
class BookOrderManagerTest {

    private BookOrderManager manager;
    private Book sampleBook;

    // Fixture setup before each test case
    @BeforeEach
    void setUp() {
        manager = new BookOrderManager();
        sampleBook = new Book("Java Programming", 100.0, 10);
        manager.addBookToInventory(sampleBook);
    }

    // Fixture teardown after each test case
    @AfterEach
    void tearDown() {
        manager = null;
        sampleBook = null;
    }

    @Test
    void testCalculateOrderTotalStandard() {
        // State testing: verifying correct mathematical calculation
        double total = manager.calculateOrderTotal("Java Programming", 2);
        assertEquals(200.0, total, 0.001, "Total should be 200.0 without discount.");
    }

    @Test
    void testCalculateOrderTotalWithDiscount() {
        manager.setDiscountRate(0.1); // 10% discount
        double total = manager.calculateOrderTotal("Java Programming", 2);
        assertEquals(180.0, total, 0.001, "Total should be 180.0 with 10% discount.");
    }

    @Test
    void testCalculateOrderTotalNullTitleThrowsException() {
        // Exception testing using assertThrows
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.calculateOrderTotal(null, 1);
        });
        assertEquals("Book title cannot be null or empty.", exception.getMessage());
    }

    // ==========================================
    // HOMEWORK SECTION: BRANCH COVERAGE EXERCISE
    // ==========================================

    @Test
    void testAddNullBookThrowsException() {
        // Covers the "book == null" true branch (the false branch is covered by setUp)
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.addBookToInventory(null);
        });
        assertEquals("Book cannot be null.", exception.getMessage());
    }

    @Test
    void testSetInvalidDiscountRateThrowsException() {
        // Just below the lower bound: "rate < 0.0" is true
        Exception below = assertThrows(IllegalArgumentException.class, () -> {
            manager.setDiscountRate(-0.01);
        });
        assertEquals("Discount rate must be between 0.0 and 1.0.", below.getMessage());

        // Just above the upper bound: "rate < 0.0" is false, "rate > 1.0" is true
        Exception above = assertThrows(IllegalArgumentException.class, () -> {
            manager.setDiscountRate(1.01);
        });
        assertEquals("Discount rate must be between 0.0 and 1.0.", above.getMessage());

        // The bounds themselves are still valid
        assertDoesNotThrow(() -> manager.setDiscountRate(0.0));
        assertDoesNotThrow(() -> manager.setDiscountRate(1.0));
    }

    @Test
    void testCalculateOrderTotalInsufficientStock() {
        // One more than the stock (10) is not allowed
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            manager.calculateOrderTotal("Java Programming", 11);
        });
        assertEquals("Insufficient stock available.", exception.getMessage());

        // Boundary: ordering exactly the whole stock is still allowed
        assertEquals(1000.0, manager.calculateOrderTotal("Java Programming", 10), 0.001);
    }

    @Test
    void testProcessCheckoutSuccessAndStockReduction() {
        boolean result = manager.processCheckout("Java Programming", 3, false);

        assertTrue(result, "Checkout with valid inputs should return true.");
        assertEquals(7, sampleBook.getStock(), "Stock should drop from 10 to 7.");
    }

    // ==========================================
    // EXTRA TESTS FOR THE REMAINING BRANCHES
    // ==========================================

    @Test
    void testCalculateOrderTotalBlankTitleThrowsException() {
        // Not null, but empty after trim(): the second half of the || condition
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.calculateOrderTotal("   ", 1);
        });
        assertEquals("Book title cannot be null or empty.", exception.getMessage());
    }

    @Test
    void testCalculateOrderTotalNonPositiveQuantityThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            manager.calculateOrderTotal("Java Programming", 0);
        });
        assertEquals("Quantity must be greater than zero.", exception.getMessage());
    }

    @Test
    void testCalculateOrderTotalBookNotFoundThrowsException() {
        // The loop checks every book without a match, then foundBook stays null
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            manager.calculateOrderTotal("Unknown Book", 1);
        });
        assertEquals("Book not found in inventory.", exception.getMessage());
    }

    @Test
    void testProcessCheckoutInvalidInputReturnsFalse() {
        assertFalse(manager.processCheckout(null, 1, false), "Null title should return false.");
        assertFalse(manager.processCheckout("Java Programming", 0, true), "Zero quantity should return false.");
        assertEquals(10, sampleBook.getStock(), "Stock must not change after a rejected checkout.");
    }

    @Test
    void testProcessCheckoutReducesOnlyTheOrderedBook() {
        // The ordered book is second in the inventory, so the loop first skips a non-matching book
        Book secondBook = new Book("Clean Code", 50.0, 5);
        manager.addBookToInventory(secondBook);

        assertTrue(manager.processCheckout("Clean Code", 2, true));
        assertEquals(3, secondBook.getStock(), "Ordered book's stock should drop from 5 to 3.");
        assertEquals(10, sampleBook.getStock(), "Other book's stock must not change.");
    }
}