package domain;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Before;
import org.junit.Test;

public class EnrollmentControlTest {
	private Student bebe;
	private Course prog;
	private Course ap;
	private Course dm;
	private Course math1;
	private Course math2;
	private Course phys1;
	private Course phys2;
	private Course maaref;
	private Course farsi;
	private Course english;
	private Course akhlagh;
	private Course economy;
	private Course karafarini;

	@Before
	public void setup() {
		math1 = new Course("4", "MATH1", 3);
		phys1 = new Course("8", "PHYS1", 3);
		prog = new Course("7", "PROG", 4);
		math2 = new Course("6", "MATH2", 3).addSamePrerequisites(math1);
		phys2 = new Course("9", "PHYS2", 3).addSamePrerequisites(math1, phys1);
		ap = new Course("2", "AP", 3).addSamePrerequisites(prog);
		dm = new Course("3", "DM", 3).addSamePrerequisites(prog);
		economy = new Course("1", "ECO", 3);
		maaref = new Course("5", "MAAREF", 2);
		farsi = new Course("12", "FA", 2);
		english = new Course("10", "EN", 2);
		akhlagh = new Course("11", "AKHLAGH", 2);
		karafarini = new Course("13", "KAR", 3);

		bebe = new Student("1", "Bebe");
	}

	private ArrayList<OfferedCourse> requestedOfferings(Course...courses) {
		Calendar cal = Calendar.getInstance();
		ArrayList<OfferedCourse> result = new ArrayList<>();
		for (Course course : courses) {
			cal.add(Calendar.DATE, 1);
			result.add(new OfferedCourse(course, cal.getTime()));
		}
		return result;
	}

	private boolean hasTaken(Student s, Course...courses) {
	    Set<Course> coursesTaken = new HashSet<>();
		for (CourseSection cs : s.getCurrentTerm())
				coursesTaken.add(cs.getCourse());
		for (Course course : courses) {
			if (!coursesTaken.contains(course))
				return false;
		}
		return true;
	}

	@Test
	public void canTakeBasicCoursesInFirstTerm() {
		EnrollmentControl enrollmentControl = new EnrollmentControl();
		List<String> result = enrollmentControl.enroll(bebe, requestedOfferings(math1, phys1, prog));
		assertNull(result);
		assertTrue(hasTaken(bebe, math1, phys1, prog));
	}

	@Test
	public void canTakeNoOfferings() {
		EnrollmentControl enrollmentControl = new EnrollmentControl();
		List<String> result = enrollmentControl.enroll(bebe, new ArrayList<>());
		assertNull(result);
		assertTrue(hasTaken(bebe));
	}

	@Test
	public void cannotTakeWithoutPreTaken() {
		EnrollmentControl enrollmentControl = new EnrollmentControl();
		List<String> errors = enrollmentControl.enroll(bebe, requestedOfferings(math2, phys1, prog));
		assertEquals(1, errors.size());
		assertEquals(errors.get(0), String.format("The student has not passed %s as a prerequisite of %s",math1.getName(), math2.getName()));
	}

	@Test
	public void cannotTakeWithoutPrePassed() {
		bebe.addTranscriptRecord(phys1, new Term("t1"), 18);
		bebe.addTranscriptRecord(prog, new Term("t1"), 12);
		bebe.addTranscriptRecord(math1, new Term("t1"), 8.4);

		EnrollmentControl enrollmentControl = new EnrollmentControl();
		List<String> errors = enrollmentControl.enroll(bebe, requestedOfferings(math2, ap));
		assertEquals(1, errors.size());
		assertEquals(errors.get(0), String.format("The student has not passed %s as a prerequisite of %s",math1.getName(), math2.getName()));
	}

	@Test
	public void canTakeWithPreFinallyPassed() {
		bebe.addTranscriptRecord(phys1, new Term("t1"), 18);
		bebe.addTranscriptRecord(prog, new Term("t1"), 12);
		bebe.addTranscriptRecord(math1, new Term("t1"), 8.4);

		bebe.addTranscriptRecord(phys2, new Term("t2"), 10);
		bebe.addTranscriptRecord(ap, new Term("t2"), 16);
		bebe.addTranscriptRecord(math1, new Term("t2"), 10.5);

		EnrollmentControl enrollmentControl = new EnrollmentControl();
		List<String> errors = enrollmentControl.enroll(bebe, requestedOfferings(math2, dm));
		assertNull(errors);
		assertTrue(hasTaken(bebe, math2, dm));
	}

	@Test
	public void cannotTakeAlreadyPassed1() {
		bebe.addTranscriptRecord(phys1, new Term("t1"), 18);
		bebe.addTranscriptRecord(prog, new Term("t1"), 12);
		bebe.addTranscriptRecord(math1, new Term("t1"), 8.4);

		bebe.addTranscriptRecord(phys2, new Term("t2"), 10);
		bebe.addTranscriptRecord(ap, new Term("t2"), 16);
		bebe.addTranscriptRecord(math1, new Term("t2"), 10.5);

		EnrollmentControl enrollmentControl = new EnrollmentControl();
		List<String> errors = enrollmentControl.enroll(bebe, requestedOfferings(math1, dm));
		assertEquals(1, errors.size());
		assertEquals(errors.get(0), String.format("The student has already passed %s", math1.getName()));
	}

	@Test
	public void cannotTakeAlreadyPassed2() {
		bebe.addTranscriptRecord(phys1, new Term("t1"), 18);
		bebe.addTranscriptRecord(prog, new Term("t1"), 12);
		bebe.addTranscriptRecord(math1, new Term("t1"), 8.4);

		bebe.addTranscriptRecord(phys2, new Term("t2"), 10);
		bebe.addTranscriptRecord(ap, new Term("t2"), 16);
		bebe.addTranscriptRecord(math1, new Term("t2"), 10.5);

		EnrollmentControl enrollmentControl = new EnrollmentControl();
		List<String> errors = enrollmentControl.enroll(bebe, requestedOfferings(phys1, dm));
		assertEquals(1, errors.size());
		assertEquals(errors.get(0), String.format("The student has already passed %s", phys1.getName()));
	}

	@Test
	public void cannotTakeOfferingsWithSameExamTime() {
		Calendar cal = Calendar.getInstance();
		EnrollmentControl enrollmentControl = new EnrollmentControl();
		List<String> errors = enrollmentControl.enroll(bebe,
				List.of(
						new OfferedCourse(phys1, cal.getTime()),
						new OfferedCourse(math1, cal.getTime())
				));
		assertEquals(1, errors.size());
		assertEquals(errors.get(0), String.format("Two offerings %s and %s have the same exam time", phys1.getName(), math1.getName()));
	}

	@Test
	public void cannotTakeOfferingsWithSameExamTimeOrACourseTwice() {
		Calendar cal = Calendar.getInstance();
		EnrollmentControl enrollmentControl = new EnrollmentControl();
		List<String> errors = enrollmentControl.enroll(bebe,
				List.of(
					new OfferedCourse(phys1, cal.getTime()),
					new OfferedCourse(math1, cal.getTime()),
					new OfferedCourse(phys1, cal.getTime())
				));
		assertEquals(2, errors.size());
		assertEquals(errors.get(0), String.format("Two offerings %s and %s have the same exam time", phys1.getName(), math1.getName()));
		assertEquals(errors.get(1), String.format("%s is requested to be taken twice", phys1.getName()));
	}

	@Test
	public void cannotTakeACourseTwice() {
		EnrollmentControl enrollmentControl = new EnrollmentControl();
		List<String> errors = enrollmentControl.enroll(bebe, requestedOfferings(phys1, math1, phys1));

		assertEquals(1, errors.size());
		assertEquals(errors.get(0), String.format("%s is requested to be taken twice", phys1.getName()));
	}

	@Test
	public void cannotTakeACourseTwiceOrWithoutPrePassed() {
		EnrollmentControl enrollmentControl = new EnrollmentControl();
		List<String> errors = enrollmentControl.enroll(bebe, requestedOfferings(phys1, dm, phys1));

		assertEquals(2, errors.size());
		assertEquals(errors.get(1), String.format("%s is requested to be taken twice", phys1.getName()));
		assertEquals(errors.get(0), String.format("The student has not passed %s as a prerequisite of %s",prog.getName(), dm.getName()));

	}

	@Test
	public void canTake14WithGPA11() {
		bebe.addTranscriptRecord(phys1, new Term("t1"), 13);
		bebe.addTranscriptRecord(prog, new Term("t1"), 11);
		bebe.addTranscriptRecord(math1, new Term("t1"), 9);

		EnrollmentControl enrollmentControl = new EnrollmentControl();
		List<String> errors = enrollmentControl.enroll(bebe, requestedOfferings(dm, math1, farsi, akhlagh, english, maaref));
		assertTrue(hasTaken(bebe, dm, math1, farsi, akhlagh, english, maaref));
		assertNull(errors);
	}

	@Test
	public void cannotTake15WithGPA11() {
		bebe.addTranscriptRecord(phys1, new Term("t1"), 13);
		bebe.addTranscriptRecord(prog, new Term("t1"), 11);
		bebe.addTranscriptRecord(math1, new Term("t1"), 9);

		EnrollmentControl enrollmentControl = new EnrollmentControl();
		List<String> errors = enrollmentControl.enroll(bebe, requestedOfferings(dm, math1, farsi, akhlagh, english, ap));
		assertEquals(1, errors.size());
		assertEquals(errors.get(0),"Number of units (15) requested does not match GPA of 11.000000");
		assertFalse(hasTaken(bebe, dm, math1, farsi, akhlagh, english, ap));
	}

	@Test
	public void canTake15WithGPA12() {
		bebe.addTranscriptRecord(phys1, new Term("t1"), 15);
		bebe.addTranscriptRecord(prog, new Term("t1"), 12);
		bebe.addTranscriptRecord(math1, new Term("t1"), 9);

		EnrollmentControl enrollmentControl = new EnrollmentControl();
		List<String> errors = enrollmentControl.enroll(bebe, requestedOfferings(dm, math1, farsi, akhlagh, english, maaref));
		assertTrue(hasTaken(bebe, dm, math1, farsi, akhlagh, english, maaref));
		assertNull(errors);
	}

	@Test
	public void canTake15WithGPA15() {
		bebe.addTranscriptRecord(phys1, new Term("t1"), 15);
		bebe.addTranscriptRecord(prog, new Term("t1"), 15);
		bebe.addTranscriptRecord(math1, new Term("t1"), 15);

		EnrollmentControl enrollmentControl = new EnrollmentControl();
		List<String> errors = enrollmentControl.enroll(bebe, requestedOfferings(dm, math2, farsi, akhlagh, english, maaref));
		assertTrue(hasTaken(bebe, dm, math2, farsi, akhlagh, english, maaref));
		assertNull(errors);
	}

	@Test
	public void cannotTake18WithGPA15() {
		bebe.addTranscriptRecord(phys1, new Term("t1"), 15);
		bebe.addTranscriptRecord(prog, new Term("t1"), 15);
		bebe.addTranscriptRecord(math1, new Term("t1"), 15);

		EnrollmentControl enrollmentControl = new EnrollmentControl();
		List<String> errors = enrollmentControl.enroll(bebe, requestedOfferings(ap, dm, math2, farsi, akhlagh, english, ap));

		assertEquals(2, errors.size());
		assertEquals(errors.get(0), "AP is requested to be taken twice");
		assertEquals(errors.get(1), "Number of units (18) requested does not match GPA of 15.000000");
		assertFalse(hasTaken(bebe, ap, dm, math2, farsi, akhlagh, english));
	}

	@Test
	public void canTake20WithGPA16() {
		bebe.addTranscriptRecord(phys1, new Term("t1"), 16);
		bebe.addTranscriptRecord(prog, new Term("t1"), 16);
		bebe.addTranscriptRecord(math1, new Term("t1"), 16);

		EnrollmentControl enrollmentControl = new EnrollmentControl();
		List<String> errors = enrollmentControl.enroll(bebe, requestedOfferings(
				ap, dm, math2, phys2, economy, karafarini, farsi));
		assertTrue(hasTaken(bebe, ap, dm, math2, phys2, economy, karafarini, farsi));
		assertNull(errors);
	}

	@Test
	public void cannotTake24() {
		bebe.addTranscriptRecord(phys1, new Term("t1"), 16);
		bebe.addTranscriptRecord(prog, new Term("t1"), 16);
		bebe.addTranscriptRecord(math1, new Term("t1"), 16);

		EnrollmentControl enrollmentControl = new EnrollmentControl();
		List<String> errors = enrollmentControl.enroll(bebe, requestedOfferings(
				ap, dm, math2, phys2, economy, karafarini, farsi, akhlagh, english));
		assertFalse(hasTaken(bebe, ap, dm, math2, phys2, economy, karafarini, farsi, akhlagh, english));
		assertEquals(1, errors.size());
		assertEquals(errors.get(0), "Number of units (24) requested does not match GPA of 16.000000");
	}


}