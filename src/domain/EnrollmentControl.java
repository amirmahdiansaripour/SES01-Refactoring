package domain;

import java.util.ArrayList;
import java.util.List;

import domain.exceptions.CourseAlreadyPassedException;
import domain.exceptions.EnrollmentRulesViolationException;
import domain.exceptions.PrerequisitesNotPassedException;
import domain.exceptions.TotalRequestedUnitsViolationException;

public class EnrollmentControl {
    private Transcript transcript;
    private List<OfferedCourse> courses;
    private ArrayList<Course> passedCourses;
    private Student student;

    public void enroll(Student student, List<OfferedCourse> courses) throws EnrollmentRulesViolationException {
        transcript = student.getTranscript();
        passedCourses = transcript.getPassedCourses();
		this.courses = courses;
        this.student = student;

        List<Exception> exceptions = new ArrayList<>();

        try { checkAlreadyPassedCourse(); } catch (CourseAlreadyPassedException e) { exceptions.add(e);}
        try { checkPrerequisitesPassed(); } catch (PrerequisitesNotPassedException e) { exceptions.add(e);}
        for (OfferedCourse offeredCourse : courses) {
            ArrayList<Course> allPassedCourses = transcript.getPassedCourses();
            for(Course passedCourse: allPassedCourses) {
                if(passedCourse.equals(offeredCourse.getCourse())) {
                    throw new EnrollmentRulesViolationException(String.format("The student has already passed %s", offeredCourse.getCourse().getName()));
                }
            }
            List<Course> prerequisites = offeredCourse.getCourse().getPrerequisites();
            nextPre:
            for (Course preRequisite : prerequisites) {
                for(Course passedCourse: allPassedCourses) {
                    if(passedCourse.equals(preRequisite))
                        continue nextPre;
                }
                throw new EnrollmentRulesViolationException(String.format("The student has not passed %s as a prerequisite of %s", preRequisite.getName(), offeredCourse.getCourse().getName()));
            }
            for (OfferedCourse possiblyConflictingOfferedCourse : courses) {
                if (offeredCourse == possiblyConflictingOfferedCourse)
                    continue;
                if (offeredCourse.getExamTime().equals(possiblyConflictingOfferedCourse.getExamTime()))
                    throw new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", offeredCourse, possiblyConflictingOfferedCourse));
                if (offeredCourse.getCourse().equals(possiblyConflictingOfferedCourse.getCourse()))
                    throw new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", offeredCourse.getCourse().getName()));
            }
        }
        try {
            checkTotalRequestedUnitsViolation();
        } catch(TotalRequestedUnitsViolationException e) {
            exceptions.add(e);
        }

        finalizeCourseSelection();

        if (!exceptions.isEmpty()) {
            throw new EnrollmentRulesViolationException(exceptions.get(0).getMessage());
        }
    }

    private int getUnitsRequested() {
        int unitsRequested = 0;
        for (OfferedCourse offeredCourse: courses) {
            unitsRequested += offeredCourse.getCourse().getUnits();
        }
        return unitsRequested;
    }

    private boolean violatesTotalRequestedUnitsRules(double gpa, int requestedUnits) {
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

    private void checkTotalRequestedUnitsViolation() throws TotalRequestedUnitsViolationException {
        int unitsRequested = getUnitsRequested();

        double gpa = transcript.calculateGPA();
        if (violatesTotalRequestedUnitsRules(gpa, unitsRequested))
            throw new TotalRequestedUnitsViolationException(unitsRequested, gpa);
    }

    private void finalizeCourseSelection() {
        student.takeCourses(courses);
    }
}
