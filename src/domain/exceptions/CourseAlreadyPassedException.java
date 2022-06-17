package domain.exceptions;

import java.util.ArrayList;

public class CourseAlreadyPassedException extends Exception {
    private final String course;

    public CourseAlreadyPassedException(String course) {
        this.course = course;
    }
    public String getCourse() {
        return course;
    }

    public String getMessage() { return String.format("The student has already passed %s", course);}
}
