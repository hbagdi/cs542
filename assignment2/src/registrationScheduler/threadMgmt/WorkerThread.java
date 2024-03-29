package registrationScheduler.threadMgmt;

import java.util.ArrayList;
import java.util.Scanner;
import registrationScheduler.data.Course;
import registrationScheduler.data.Student;
import registrationScheduler.pool.CoursePool;
import registrationScheduler.pool.ObjectPoolInterface;
import registrationScheduler.store.Results;
import registrationScheduler.store.StoreInterface;
import registrationScheduler.util.FileProcessor;
import registrationScheduler.util.Logger;
import registrationScheduler.util.Logger.DebugLevel;

/**
 * @author Hardik Bagdi (hbagdi1@binghamton.edu)
 *
 */
public class WorkerThread implements Runnable {
	private FileProcessor fileProcessor;
	private StoreInterface store;
	private ObjectPoolInterface coursePool;
	private String threadName;
	private Scanner scanner;
	private ArrayList<Student> pendingStudents;

	/**
	 * @param threadName
	 * @param fileProcessor_in
	 * @param results_in
	 * @param coursePool_in
	 */
	public WorkerThread(String threadName_in, FileProcessor fileProcessor_in, StoreInterface results_in,
			ObjectPoolInterface coursePool_in) {
		Logger.writeMessage("WorkerThread constructor called", DebugLevel.CONSTRUCTOR);
		if (fileProcessor_in == null || results_in == null || coursePool_in == null) {
			throw new IllegalArgumentException();
		}
		threadName = threadName_in;
		fileProcessor = fileProcessor_in;
		coursePool = coursePool_in;
		store = results_in;
		pendingStudents = new ArrayList<>();
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Student student;
		try {
			Logger.writeMessage(threadName + ": run() called.", DebugLevel.THREAD);
			String line = null;
			int i = 0;
			while ((line = fileProcessor.getLine()) != null) {
				i++;
				student = inputParser(line);
				allocate(student);
				if (student.hasAllCourses()) {
					store.putStudent(student);
				} else {
					pendingStudents.add(student);
				}
			}
			while (!pendingStudents.isEmpty()) {
				shuffle();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {

		}
	}

	private void shuffle() {
		Student studentInNeed = pendingStudents.remove((int) Math.random() * pendingStudents.size());
		// allocate this unlucky student any courses that are available
		for (Course course : Course.values()) {
			if (!studentInNeed.hasAllCourses() && !studentInNeed.hasCourse(course)) {
				if (coursePool.borrowCourse(course)) {
					studentInNeed.addCourse(course);
				}
			}
		}
		if (studentInNeed.hasAllCourses()) {
			store.putStudent(studentInNeed);
			return;
		}
		// now we will attempt to shuffle courses between students
		while (!studentInNeed.hasAllCourses()) {
			// take up a random student who might help our unlucky student
			Student studentHelper = store.deleteRandomStudent();
			// get his courses
			Course[] courses = studentHelper.getCoursesAlloted();
			for (Course course : courses) {
				// check if the helper student has any course which the InNeed
				// student can use
				if (!studentInNeed.hasCourse(course)) {
					// helper student can help the inNeed student if helper can
					// get
					// some other course
					Course newCourseForHelper = searchCourseForStudent(studentHelper);
					if (newCourseForHelper != null) {
						// Fire in the hole!
						// transfer this course
						studentHelper.removeCourse(course);
						studentInNeed.addCourse(course);
						// give helper student new course
						studentHelper.addCourse(newCourseForHelper);
					} else {
						// helper student herself can't have another course, try
						// another helper student
					}
					break;
				}
			}
			// put helper student back in the results no matter what
			store.putStudent(studentHelper);
		}
		store.putStudent(studentInNeed);
	}

	private Course searchCourseForStudent(Student studentHelper) {
		for (Course course : Course.values()) {
			if (!studentHelper.hasCourse(course) && coursePool.borrowCourse(course)) {
				return course;
			}
		}
		return null;
	}

	private void allocate(Student student) {
		Course course;
		try {

			for (int i = 0; i < Student.requriedCourses; i++) {
				// if the course is available,then give it to him
				course = student.getCourseByPreferenceRank(i + 1);
				if (coursePool.borrowCourse(course)) {
					student.addCourse(course);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {

		}

	}

	/**
	 * @param line
	 *            denoted the student name and preference for various courses
	 * @return the student object that was constructed by the parameter
	 *         mentioned above
	 */
	private Student inputParser(String line) {
		Student student = null;
		String name;
		int[] preferences = new int[Course.totalCourses];
		try {
			scanner = new Scanner(line);
			name = scanner.next();
			for (int i = 0; i < preferences.length; i++) {
				preferences[i] = scanner.nextInt();
			}
			student = new Student(name, preferences);
		} catch (Exception e) {
			System.err.println("InputParser failed.\n");
			e.printStackTrace();
			System.exit(1);
		} finally {
			scanner.close();
		}
		return student;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String status = "WorkerThread Status:\n";
		status.concat("Processing File: " + fileProcessor.getFilename());
		// TODO add other data structure info here
		return status;
	}

}