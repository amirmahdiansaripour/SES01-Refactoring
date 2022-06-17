package domain;

import java.util.ArrayList;
import java.util.List;

import domain.exceptions.CourseAlreadyPassedException;
import domain.exceptions.EnrollmentRulesViolationException;
import domain.exceptions.PrerequisitesNotPassedException;

public class EnrollmentControl {
    private List<OfferedCourse> courses;
    private ArrayList<Course> passedCourses;

    public void enroll(Student student, List<OfferedCourse> courses) throws EnrollmentRulesViolationException {
        List<Exception> exceptions = new ArrayList<>();
        Transcript transcript = student.getTranscript();

        passedCourses = transcript.getPassedCourses();
        this.courses = courses;

        try { checkAlreadyPassedCourse(); } catch (CourseAlreadyPassedException e) { exceptions.add(e);}
        try { checkPrerequisitesPassed(); } catch (PrerequisitesNotPassedException e) { exceptions.add(e);}

        for (OfferedCourse o : courses) {
            for (OfferedCourse o2 : courses) {
                if (o == o2)
                    continue;
                if (o.getExamTime().equals(o2.getExamTime()))
                    throw new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", o, o2));
                if (o.getCourse().equals(o2.getCourse()))
                    throw new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", o.getCourse().getName()));
            }
		}
		int unitsRequested = 0;
		for (OfferedCourse o : courses)
			unitsRequested += o.getCourse().getUnits();

		double gpa = transcript.calculateGPA();
		if ((gpa < 12 && unitsRequested > 14) ||
				(gpa < 16 && unitsRequested > 16) ||
				(unitsRequested > 20))
			throw new EnrollmentRulesViolationException(String.format("Number of units (%d) requested does not match GPA of %f", unitsRequested, gpa));
		for (OfferedCourse o : courses)
			student.takeCourse(o.getCourse(), o.getSection());

        if (!exceptions.isEmpty()) {
            throw new EnrollmentRulesViolationException(exceptions.get(0).getMessage());
        }
	}

    public void checkAlreadyPassedCourse() throws CourseAlreadyPassedException {
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

}
