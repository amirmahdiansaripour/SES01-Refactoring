package domain;
import java.util.Date;

public class OfferedCourse {
	private final Course course;
	private final int section;
	private final Date examDate;

	public OfferedCourse(Course course) {
		this.course = course;
		this.section = 1;
		this.examDate = java.sql.Date.valueOf(java.time.LocalDate.now());
	}

	public OfferedCourse(Course course, Date examDate) {
		this.course = course;
		this.section = 1;
		this.examDate = examDate;
	}

	public OfferedCourse(Course course, Date examDate, int section) {
		this.course = course;
		this.section = section;
		this.examDate = examDate;
	}
	
	public Course getCourse() {
		return course;
	}

	public Date getExamTime() {
		return examDate;
	}

	public int getSection() { return section; }

	public String toString() {
		return course.getName() + " - " + section;
	}
}
