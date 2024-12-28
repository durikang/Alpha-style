package page.admin.admin.member.exception;

public class DuplicateMemberException extends RuntimeException {
    public DuplicateMemberException(String message) {
        super(message);
    }
}