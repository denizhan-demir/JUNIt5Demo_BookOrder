package nye.valyis.progtech;

import java.util.ArrayList;
import java.util.List;

// ==========================================
// 2. CLASS UNDER TEST: BookOrderManager
// ==========================================
public class BookOrderManager {
    private List<Book> inventory = new ArrayList<>();
    private double discountRate = 0.0;

    // Adds a book to the inventory with null validation (White-box / boundary principle)
    public void addBookToInventory(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }
        inventory.add(book);
    }

    // Sets discount rate with boundary validation (0.0 to 1.0)
    public void setDiscountRate(double rate) {
        if (rate < 0.0 || rate > 1.0) {
            throw new IllegalArgumentException("Discount rate must be between 0.0 and 1.0.");
        }
        this.discountRate = rate;
    }

    // Calculates total price for an order, validating inputs, stock, and applying discounts
    public double calculateOrderTotal(String bookTitle, int quantity) {
        if (bookTitle == null || bookTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be null or empty.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        Book foundBook = null;
        for (Book b : inventory) {
            if (b.getTitle().equals(bookTitle)) {
                foundBook = b;
                break;
            }
        }

        if (foundBook == null) {
            throw new IllegalStateException("Book not found in inventory.");
        }

        if (foundBook.getStock() < quantity) {
            throw new IllegalStateException("Insufficient stock available.");
        }

        double total = foundBook.getPrice() * quantity;
        if (discountRate > 0) {
            total = total * (1.0 - discountRate);
        }
        return total;
    }

    // Processes checkout and updates inventory stock
    public boolean processCheckout(String bookTitle, int quantity, boolean expressShipping) {
        if (bookTitle == null || quantity <= 0) {
            return false;
        }
        double total = calculateOrderTotal(bookTitle, quantity);

        for (Book b : inventory) {
            if (b.getTitle().equals(bookTitle)) {
                b.setStock(b.getStock() - quantity);
                break;
            }
        }
        return true;
    }
}
