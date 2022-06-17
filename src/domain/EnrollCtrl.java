package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import domain.exceptions.EnrollmentRulesViolationException;

public class EnrollCtrl {
	public void enroll(Student student, List<Exam> courses) throws EnrollmentRulesViolationException {
        Transcript transcript = student.getTranscript();
		for (Exam o : courses) {
            ArrayList<Course> allPassedCourses = transcript.getPassedCourses();
            for(Course passedCourse: allPassedCourses) {
                if(passedCourse.equals(o.getCourse())) {
                    throw new EnrollmentRulesViolationException(String.format("The student has already passed %s", o.getCourse().getName()));
                }
            }
			List<Course> prerequisites = o.getCourse().getPrerequisites();
			nextPre:
			for (Course preRequisite : prerequisites) {
                for(Course passedCourse: allPassedCourses) {
                    if(passedCourse.equals(preRequisite))
                        continue nextPre;
                }
				throw new EnrollmentRulesViolationException(String.format("The student has not passed %s as a prerequisite of %s", preRequisite.getName(), o.getCourse().getName()));
			}
            for (Exam o2 : courses) {
                if (o == o2)
                    continue;
                if (o.getExamTime().equals(o2.getExamTime()))
                    throw new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", o, o2));
                if (o.getCourse().equals(o2.getCourse()))
                    throw new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", o.getCourse().getName()));
            }
		}
		int unitsRequested = 0;
		for (Exam o : courses)
			unitsRequested += o.getCourse().getUnits();

		double gpa = transcript.calculateGPA();
		if ((gpa < 12 && unitsRequested > 14) ||
				(gpa < 16 && unitsRequested > 16) ||
				(unitsRequested > 20))
			throw new EnrollmentRulesViolationException(String.format("Number of units (%d) requested does not match GPA of %f", unitsRequested, gpa));
		for (Exam o : courses)
			student.takeCourse(o.getCourse(), o.getSection());
	}
}
