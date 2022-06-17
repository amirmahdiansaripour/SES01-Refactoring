package domain;
import java.util.Date;

public class Exam {
	private Course course;
	private int section;
	private Date examDate;

	public Exam(Course course) {
		this.course = course;
		this.section = 1;
		this.examDate = java.sql.Date.valueOf(java.time.LocalDate.now());
	}

	public Exam(Course course, Date examDate) {
		this.course = course;
		this.section = 1;
		this.examDate = examDate;
	}

	public Exam(Course course, Date examDate, int section) {
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
