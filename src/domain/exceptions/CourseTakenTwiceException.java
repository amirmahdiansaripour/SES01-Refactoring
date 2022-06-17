package domain.exceptions;

public class CourseTakenTwiceException extends Exception {
    private final String course;

    public CourseTakenTwiceException(String course) {
        this.course = course;
    }

    public String getMessage() { return String.format("%s is requested to be taken twice", course);}
}
