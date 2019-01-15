package com.bits.studentrecords;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Iterator;

public class StudentRecords {

	// constant used for finding department wise max and average
	final int CSE = 0;
	final int MEC = 1;
	final int ARC = 2;
	final int ECE = 3;

	public static void main(String[] args) {
		StudentRecords studentRecords = new StudentRecords();
		StudentHash records = new StudentHash();

		studentRecords.initializeHash(records);
		studentRecords.insertRecordsFromFile(records);
		studentRecords.hallOfFame(records, 8.5f);
		studentRecords.newCourseList(records, 7.0f, 8.0f);
		studentRecords.depAvg(records);
		studentRecords.destroyHash(records);
	}

	public void initializeHash(StudentHash records) {
		records.initialize();
	}

	// Insert a single record in table
	public void insertStudentRec(StudentHash records, String studentId, float cgpa) {
		records.insert(studentId, cgpa);
	}

	public void hallOfFame(StudentHash records, float CGPA) {

		if (records.size() > 0) {

			PrintWriter writer = null;
			try {
				Iterator<StudentRecord> it = records.iterator();
				writer = new PrintWriter("halloffame.txt", "UTF-8");
				while (it.hasNext()) {
					StudentRecord record = it.next();
					String studentId = record.getStudentId();
					// get CGPA of each record
					float studentCGPA = record.getCgpa();

					// Add student id to hall of fame
					if (studentCGPA > CGPA) {
						writer.println(studentId);
					}
				}

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (writer != null)
					writer.close();
			}

		} else {
			System.out.println("No record present in table");
		}

	}

	public void newCourseList(StudentHash records, float cgpaFrom, float cgpaTo) {

		if (records.size() > 0) {
			PrintWriter writer = null;
			try {
				writer = new PrintWriter("courseOffer.txt", "UTF-8");
				Iterator<StudentRecord> it = records.iterator();
				while (it.hasNext()) {
					StudentRecord record = it.next();
					String studentId = record.getStudentId();
					// get CGPA of each record
					float studentCGPA = record.getCgpa();
					int admissionYear = Integer.parseInt(studentId.substring(0, 4));
					// Last 5 years 2013-2017
					if (admissionYear >= 2013) {
						if (studentCGPA >= cgpaFrom && studentCGPA <= cgpaTo) {
							writer.println(studentId);
						}
					}
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (writer != null)
					writer.close();
			}

		} else {
			System.out.println("No record present in table");
		}
	}


	public void depAvg(StudentHash records) {

		// take temporary data structure
		class DepatmentData {
			float sum;
			float max;
			int totalNo;
		}

		// array to hold data for 4 departments
		DepatmentData[] data = new DepatmentData[4];
		for (int i = 0; i < 4; i++) {
			data[i] = new DepatmentData();
		}
		if (records.size() > 0) {

			// Read records line by line
			PrintWriter writer = null;
			try {
				writer = new PrintWriter("departmentAverage.txt", "UTF-8");
				Iterator<StudentRecord> it = records.iterator();
				while (it.hasNext()) {
					StudentRecord record = it.next();
					String studentId = record.getStudentId();
					// get CGPA of each record
					float studentCGPA = record.getCgpa();
					String studentCourse = studentId.substring(4, 7);

					// get index equivalent to course
					int id = getIdForCourse(studentCourse);
					if (id != -1) {

						data[id].sum += studentCGPA;
						data[id].totalNo++;

						if (data[id].max < studentCGPA) {
							data[id].max = studentCGPA;
						}
					}
				}
				// format average to 2 places of decimal value
				DecimalFormat df = new DecimalFormat("0.00");
				writer.println("CSE, Max: " + data[CSE].max + " ,Avg: " + df.format(data[CSE].sum / data[CSE].totalNo));
				writer.println("MEC, Max: " + data[MEC].max + " ,Avg: " + df.format(data[MEC].sum / data[MEC].totalNo));
				writer.println("ARC, Max: " + data[ARC].max + " ,Avg: " + df.format(data[ARC].sum / data[ARC].totalNo));
				writer.println("ECE, Max: " + data[ECE].max + " ,Avg: " + df.format(data[ECE].sum / data[ECE].totalNo));

			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				writer.close();
			}

		} else {
			System.out.println("No record present in table");
		}
	}

	public void destroyHash(StudentHash records) {
		records.deinitialize();
	}
	
	private void insertRecordsFromFile(StudentHash records) {

		// Read file to get records
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("input.txt"));
			String record;
			while ((record = br.readLine()) != null) {
				String studentId = record.substring(0, 11);
				float CGPA = Float.valueOf(record.substring(14, 17));
				// Insert records one by one in hash table
				insertStudentRec(records, studentId, CGPA);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private int getIdForCourse(String course) {

		int id = -1;
		if (course.equals("CSE"))
			id = CSE;
		if (course.equals("MEC"))
			id = MEC;
		if (course.equals("ARC"))
			id = ARC;
		if (course.equals("ECE"))
			id = ECE;

		return id;
	}

}
