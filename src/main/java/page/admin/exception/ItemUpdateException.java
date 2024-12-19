package page.admin.exception;

public class ItemUpdateException extends RuntimeException {
    public ItemUpdateException(String message) {
        super(message);
    }

    public ItemUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
