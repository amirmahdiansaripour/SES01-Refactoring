package domain.exceptions;

public class TotalRequestedUnitsViolationException extends Exception{
    public TotalRequestedUnitsViolationException(int requestedUnits, double gpa) {
        super(String.format("Number of units (%d) requested does not match GPA of %f", requestedUnits, gpa));
    }
}
