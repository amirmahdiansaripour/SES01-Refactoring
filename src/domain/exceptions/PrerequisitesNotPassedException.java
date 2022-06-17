package domain.exceptions;

public class PrerequisitesNotPassedException extends Exception{
    private final String course;
    private final String prerequisite;

    public PrerequisitesNotPassedException(String prerequisite, String course) {
        this.course = course;
        this.prerequisite = prerequisite;
    }
    public String getMessage() { return String.format("The student has not passed %s as a prerequisite of %s", prerequisite, course);}
}
