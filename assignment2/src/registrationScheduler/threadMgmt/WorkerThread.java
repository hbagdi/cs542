package registrationScheduler.threadMgmt;

import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.stream.events.StartDocument;

import registrationScheduler.data.Course;
import registrationScheduler.data.Student;
import registrationScheduler.pool.CoursePool;
import registrationScheduler.store.Results;
import registrationScheduler.util.FileProcessor;
import registrationScheduler.util.Logger;
import registrationScheduler.util.Logger.DebugLevel;

/**
 * @author Hardik Bagdi (hbagdi1@binghamton.edu)
 *
 */
public class WorkerThread implements Runnable {
	private FileProcessor fileProcessor;
	private Results results;
	private CoursePool coursePool;
	private String threadName;
	private Scanner scanner;
	private ArrayList<Student> students;

	/**
	 * @param threadName
	 * @param fileProcessor_in
	 * @param results_in
	 * @param coursePool_in
	 */
	public WorkerThread(String threadName_in, FileProcessor fileProcessor_in, Results results_in,
			CoursePool coursePool_in) {
		Logger.writeMessage("WorkerThread constructor called", DebugLevel.CONSTRUCTOR);
		if (fileProcessor_in == null || results_in == null || coursePool_in == null) {
			throw new IllegalArgumentException();
		}
		threadName = threadName_in;
		fileProcessor = fileProcessor_in;
		coursePool = coursePool_in;
		results = results_in;
		students = new ArrayList<>();
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
			while ((line = fileProcessor.getLine()) != null) {
				student = inputParser(line);

				allocate(student);
				students.add(student);
			}
			if (!allStudentsHaveCourses()) {
				System.out.println("SHIT HAPPENS!");
				System.exit(1);
			} else {
				writeToResults();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {

		}
	}

	private boolean allStudentsHaveCourses() {
		int i = 0;
		for (Student student : students) {
			if (student.hasAllCourses()){
				i++;//System.out.println("has all courses:"+student);
				//continue;
			}
			else
				return false;
		}
		
		return true;
	}

	private void writeToResults() throws IllegalAccessException {
		if (!allStudentsHaveCourses())
			throw new IllegalAccessException();
		for (Student student : students)
			results.putStudent(student);
	}

	private void allocate(Student student) {
		Course course;
		try {

			for (int i = 0; i < Student.requriedCourses; i++) {
				// if the course is available,then give it to him
				course = student.getCourseByPreferenceRank(i + 1);
				if (coursePool.getCourse(course)) {
					student.addCourse(course);				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {

		}

	}

	/**
	 * @param line
	 * @return
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