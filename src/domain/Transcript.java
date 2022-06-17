package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Transcript {
    private Map<Term, TermGradeReport> transcriptMap = new HashMap<>();

    public Transcript() {}

    public ArrayList<Course> getPassedCourses() {
        ArrayList<Course> passedCourses = new ArrayList<>();
        for(Map.Entry<Term, TermGradeReport> termGradeReport: transcriptMap.entrySet()) {
            passedCourses.addAll(termGradeReport.getValue().getPassedCourses());
        }
        return passedCourses;
    }

    public void addTranscriptRecord(Course course, Term term, double grade) {
        if(!transcriptMap.containsKey(term))
            transcriptMap.put(term, new TermGradeReport());
        transcriptMap.get(term).addGrade(course, grade);
    }

    private double getPoints() {
        double points = 0;
        for(Map.Entry<Term, TermGradeReport> termReport: transcriptMap.entrySet()) {
            TermGradeReport termGradeReport = termReport.getValue();
            points += termGradeReport.getPoints();
        }
        return points;
    }

    private int getTotalUnits() {
        int totalUnits = 0;
        for(Map.Entry<Term, TermGradeReport> termReport: transcriptMap.entrySet()) {
            TermGradeReport termGradeReport = termReport.getValue();
            totalUnits += termGradeReport.getTotalUnits();
        }
        return totalUnits;
    }

    public double calculateGPA() {
        return getPoints() / getTotalUnits();
    }

}
