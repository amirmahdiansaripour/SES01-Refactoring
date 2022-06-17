package domain;

import java.util.ArrayList;
import java.util.List;

import domain.exceptions.CourseAlreadyPassedException;
import domain.exceptions.EnrollmentRulesViolationException;

public class EnrollmentControl {
    private Transcript transcript;
    private List<OfferedCourse> courses;
    private List<Exception> exceptions;

	public void enroll(Student student, List<OfferedCourse> courses) throws EnrollmentRulesViolationException {
        transcript = student.getTranscript();
		this.courses = courses;
        exceptions = new ArrayList<>();

        try { checkAlreadyPassedCourse(); } catch (CourseAlreadyPassedException e) { exceptions.add(e);}

        for (OfferedCourse o : courses) {
            ArrayList<Course> allPassedCourses = transcript.getPassedCourses();
            for(Course passedCourse: allPassedCourses) {
                if(passedCourse.equals(o.getCourse())) {
                    throw new EnrollmentRulesViolationException(String.format("The student has already passed %s", o.getCourse().getName()));
                }
            }
			List<Course> prerequisites = o.getCourse().getPrerequisites();
			nextPre:
			for (Course pre : prerequisites) {
                for(Course passedCourse: allPassedCourses) {
                    if(passedCourse.equals(pre))
                        continue nextPre;
                }
				throw new EnrollmentRulesViolationException(String.format("The student has not passed %s as a prerequisite of %s", pre.getName(), o.getCourse().getName()));
			}
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
            ArrayList<Course> passedCourses = transcript.getPassedCourses();
            for (Course passedCourse : passedCourses) {
                if (passedCourse.equals(course.getCourse())) {
                    throw new CourseAlreadyPassedException(course.getCourse().getName());
                }
            }
        }
    }
}
