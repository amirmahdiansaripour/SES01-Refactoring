package domain;

import java.util.ArrayList;
import java.util.List;

import domain.exceptions.*;

public class EnrollmentControl {
    private Transcript transcript;
    private List<OfferedCourse> courses;
    private ArrayList<Course> passedCourses;
    private Student student;

    public List<String> enroll(Student student, List<OfferedCourse> courses) {
        transcript = student.getTranscript();
        passedCourses = transcript.getPassedCourses();
		this.courses = courses;
        this.student = student;

        List<Exception> exceptions = new ArrayList<>();

        try { checkAlreadyPassedCourse(); } catch (CourseAlreadyPassedException e) { exceptions.add(e);}
        try { checkPrerequisitesPassed(); } catch (PrerequisitesNotPassedException e) { exceptions.add(e);}
        try { checkExamConflict(); } catch (ExamTimesConflictException e) { exceptions.add(e);}
        try { checkCourseTakenTwice(); } catch (CourseTakenTwiceException e) { exceptions.add(e);}
        try {
            checkTotalRequestedUnitsRulesViolated();
            finalizeCourseSelection();
        } catch(TotalRequestedUnitsViolationException e) { exceptions.add(e);}

        if (!exceptions.isEmpty()) {
            return getErrorMessages(exceptions);
        }

        return null;
    }

    private int getUnitsRequested() {
        int unitsRequested = 0;
        for (OfferedCourse offeredCourse: courses) {
            unitsRequested += offeredCourse.getCourse().getUnits();
        }
        return unitsRequested;
    }

    private boolean violatedTotalRequestedUnitsRules(double gpa, int requestedUnits) {
        return (gpa < 12 && requestedUnits > 14) || (gpa < 16 && requestedUnits > 16) || (requestedUnits > 20);
    }

    private void checkAlreadyPassedCourse() throws CourseAlreadyPassedException {
        for (OfferedCourse course : courses) {
            for (Course passedCourse : passedCourses) {
                if (passedCourse.equals(course.getCourse())) {
                    throw new CourseAlreadyPassedException(course.getCourse().getName());
                }
            }
        }
    }

    public void checkPrerequisitesPassed() throws PrerequisitesNotPassedException {
        for (OfferedCourse course : courses) {
            List<Course> prerequisites = course.getCourse().getPrerequisites();
            for (Course preRequisite : prerequisites) {
                if (!passedCourses.contains(preRequisite)) {
                    throw new PrerequisitesNotPassedException(preRequisite.getName(), course.getCourse().getName());
                }
            }
        }
    }

    public void checkExamConflict() throws ExamTimesConflictException {
        for (OfferedCourse course1 : courses) {
            for (OfferedCourse course2 : courses) {
                if (course1 != course2 && course1.getExamTime().equals(course2.getExamTime()))
                    throw new ExamTimesConflictException(course1.getCourse().getName(), course2.getCourse().getName());
            }
        }
    }

    public void checkCourseTakenTwice() throws CourseTakenTwiceException {
        for (OfferedCourse course1 : courses) {
            for (OfferedCourse course2 : courses) {
                if (course1 != course2 && course1.getCourse().equals(course2.getCourse()))
                    throw new CourseTakenTwiceException(course1.getCourse().getName());
            }
        }
    }

    private void checkTotalRequestedUnitsRulesViolated() throws TotalRequestedUnitsViolationException {
        int unitsRequested = getUnitsRequested();

        double gpa = transcript.calculateGPA();
        if (violatedTotalRequestedUnitsRules(gpa, unitsRequested))
            throw new TotalRequestedUnitsViolationException(unitsRequested, gpa);
    }

    private void finalizeCourseSelection() {
        student.takeCourses(courses);
    }

    private List<String> getErrorMessages(List<Exception> exceptions){
        List<String> errorMessages = new ArrayList<>();
        for (Exception e: exceptions) {
            errorMessages.add(e.getMessage());
        }
        return errorMessages;
    }
}
