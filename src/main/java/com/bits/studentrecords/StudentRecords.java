package com.bits.studentrecords;

import java.io.*;
import java.text.DecimalFormat;

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

	public void insertRecordsFromFile(StudentHash records) {

		// Read file to get records
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("input.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Read records line by line
		String record;
		try {
			while ((record = br.readLine()) != null) {
				String studentId = record.substring(0, 11);
				float CGPA = Float.valueOf(record.substring(14, 17));

				// Insert records one by one in hash table
				insertStudentRec(records, studentId, CGPA);
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Insert a single record in table
	private void insertStudentRec(StudentHash records, String studentId, float CGPA) {
		records.insert(studentId, CGPA);
	}

	public void hallOfFame(StudentHash records, float CGPA) {

		if (records.size() > 0) {

			// Read file to get records
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader("input.txt"));
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}

			// Read records line by line
			String record;
			PrintWriter writer;
			try {
				writer = new PrintWriter("halloffame.txt", "UTF-8");

				while ((record = br.readLine()) != null) {
					String studentId = record.substring(0, 11);

					// get CGPA of each record
					float studentCGPA = records.getStudentCGPA(studentId);

					// Add student id to hall of fame
					if (studentCGPA > CGPA) {
						writer.println(studentId);
					}
				}
				writer.close();
				br.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			System.out.println("No record present in table");
		}

	}

	void newCourseList(StudentHash records, float CGPAFrom, float CPGATo) {

		if (records.size() > 0) {

			// Read file to get records
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader("input.txt"));
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			}

			// Read records line by line
			String record;
			PrintWriter writer;
			try {
				writer = new PrintWriter("courseOffer.txt", "UTF-8");

				while ((record = br.readLine()) != null) {

					String studentId = record.substring(0, 11);
					int admissionYear = Integer.parseInt(studentId.substring(0, 4));

					// Last 5 years 2013-2017
					if (admissionYear >= 2013) {

						// get CGPA of each record
						float studentCGPA = records.getStudentCGPA(studentId);

						// add student id to course offer, if CGPA falls in given range
						if (studentCGPA >= CGPAFrom && studentCGPA <= CPGATo) {
							writer.println(studentId);
						}
					}
				}
				writer.close();
				br.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			System.out.println("No record present in table");
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

	public void depAvg(StudentHash records) {

		// take temporary data structure
		class DepatmentData {
			float sum;
			float max;
			int totalNo;
		}

		// array to hold data for 4 departments
		DepatmentData[] data = new DepatmentData[4];

		if (records.size() > 0) {

			// Read records line by line
			String record;
			PrintWriter writer;
			try {
				BufferedReader br = new BufferedReader(new FileReader("input.txt"));
				writer = new PrintWriter("departmentAverage.txt", "UTF-8");

				// allocate memory
				for (int i = 0; i < 4; i++) {
					data[i] = new DepatmentData();
				}

				while ((record = br.readLine()) != null) {

					String studentId = record.substring(0, 11);
					String studentCourse = studentId.substring(4, 7);

					float studentCGPA = records.getStudentCGPA(studentId);

					// get id equivalent to course
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

				writer.close();
				br.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			System.out.println("No record present in table");
		}
	}

	void destroyHash(StudentHash records) {

		records.deinitialize();
	}

}
