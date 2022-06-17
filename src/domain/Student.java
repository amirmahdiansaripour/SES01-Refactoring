package domain;
import java.util.ArrayList;
import java.util.List;

public class Student {
	private final String id;
	private final String name;
	private final Transcript transcript;
	private final List<CourseSection> currentTerm;

	public Student(String id, String name) {
		this.id = id;
		this.name = name;
		this.transcript = new Transcript();
		this.currentTerm = new ArrayList<>();
	}

	public Transcript getTranscript() {
		return transcript;
	}

	public void takeCourse(Course course, int section) {
		currentTerm.add(new CourseSection(course, section));
	}

	public void takeCourses(List<OfferedCourse> newCourses) {
		for (OfferedCourse offeredCourse: newCourses)
			takeCourse(offeredCourse.getCourse(), offeredCourse.getSection());
	}

	public void addTranscriptRecord(Course course, Term term, double grade) {
	    transcript.addTranscriptRecord(course, term, grade);
    }

    public List<CourseSection> getCurrentTerm() {
        return currentTerm;
    }

    public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
}
