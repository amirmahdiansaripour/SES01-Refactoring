package domain;

public class CourseSection {
    private Course course;
    private int section;

    CourseSection(Course course, int section) {
        this.course = course;
        this.section = section;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }
}
