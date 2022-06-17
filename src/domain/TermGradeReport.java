package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TermGradeReport {
    Map<Course, Double> termReport = new HashMap<>();

    public TermGradeReport() {}

    public void addGrade(Course course, double grade) {
        termReport.put(course, grade);
    }

    public ArrayList<Course> getPassedCourses() {
        ArrayList<Course> passedCourses = new ArrayList<>();
        for(Map.Entry<Course, Double> courseReport: termReport.entrySet()) {
            if(courseReport.getValue() >= 10)
                passedCourses.add(courseReport.getKey());
        }
        return passedCourses;
    }

    public int getTotalUnits() {
        int totalUnits = 0;
        for(Map.Entry<Course, Double> courseReport: termReport.entrySet()) {
            Course course = courseReport.getKey();
            totalUnits += course.getUnits();
        }
        return totalUnits;
    }

    public double getPoints() {
        double points = 0;
        for(Map.Entry<Course, Double> courseReport: termReport.entrySet()) {
            Course course = courseReport.getKey();
            Double grade = courseReport.getValue();
            points += grade * course.getUnits();
        }
        return points;
    }
}
