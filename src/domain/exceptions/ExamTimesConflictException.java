package domain.exceptions;

public class ExamTimesConflictException extends Exception{
    private final String course1;
    private final String course2;

    public ExamTimesConflictException(String course1, String course2) {
        this.course1 = course1;
        this.course2 = course2;
    }

    public String getMessage() { return String.format("Two offerings %s and %s have the same exam time", course1, course2);
    }
}
