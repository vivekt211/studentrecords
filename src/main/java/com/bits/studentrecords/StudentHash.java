package com.bits.studentrecords;

import java.util.Iterator;

import com.bits.studentrecords.StudentHash.CollissionResolutionStrategy;

public class StudentHash implements Iterable<StudentRecord> {

	static final int HASH_WEIGHT = 31;
	static final int COMPRESSION_A = 3;
	static final int COMPRESSION_B = 269;
	static final float DEFAULT_LOAD_FACTOR = 0.75f;

	enum CollissionResolutionStrategy {
		SEPARATE_CHAINING, LINEAR_PROBING
	}

	/**
	 * By Changing the value of this field the collosionResolutionStrategy can
	 * be configured. Please change it as per your need
	 */
	private static final CollissionResolutionStrategy resolutionStrategy = CollissionResolutionStrategy.LINEAR_PROBING;

	private int capacity = 32;
	private StudentRecord[] recordTable;
	private Node[] recordNodes;
	private Node linkedNodes;
	private int recordCount = 0;

	public int size() {
		return recordCount;
	}

	public int actualRecordCount() {
		return recordNodes.length;
	}

	private void initialize(int newCapacity) {
		if (resolutionStrategy == CollissionResolutionStrategy.SEPARATE_CHAINING) {
			recordNodes = new Node[newCapacity];
			capacity = newCapacity;
		} else {
			recordTable = new StudentRecord[newCapacity];
			capacity = newCapacity;
		}
	}

	public void initialize() {
		initialize(capacity);
	}

	/*
	 * check if input student id is valid according to required format ie
	 * YYYYAAADDDD
	 */
	private boolean isStudentIdValid(String studentId) {

		boolean isValid = false;
		// student id length check
		try {
			if (studentId.length() == 11) {
				// student id admission year check
				int admissionYear = Integer.parseInt(studentId.substring(0, 4));
				if (admissionYear >= 2008 && admissionYear <= 2018) {
					// student id course check
					String studentCourse = studentId.substring(4, 7);
					if (studentCourse == "CSE" || studentCourse == "MEC" || studentCourse == "ARC"
							|| studentCourse == "ECE") {
					}
					int rollNo = Integer.parseInt(studentId.substring(7, 11));
					if (rollNo >= 0 && rollNo <= 9999) {
						isValid = true;
					}
				}
			}
		} catch (NumberFormatException ex) {
			return false;
		}
		return isValid;
	}

	/*
	 * Compute the hash code map from string type to integer type
	 * 
	 * In some cases, we may need a hash function that guarantees that the
	 * probabilities that larger numbers of hash values collide is the same as
	 * what one would get with a random function. One way to achieve such a
	 * result for an integer key, k, is to use a random polynomial hash
	 * function, h, which is defined as
	 * 
	 * <code> h(k) = ad + k(ad-1 + k(ad-2 + · · · + k(a3 + k(a2 + ka1)) · · · ))
	 * mod N </code>
	 *
	 */
	private long computeHashCode(String studentId) {

		// copy the last character
		long hashCode = Long.parseLong(studentId.substring(studentId.length() - 1));
		long hashWeight = HASH_WEIGHT;

		for (int i = studentId.length() - 2; i >= 0; i--) {
			hashCode = hashCode + (Character.getNumericValue((studentId.charAt(i))) * hashWeight);
			hashWeight = hashWeight * HASH_WEIGHT;
		}
		return hashCode;
	}

	// compute compression code map
	private int computeCompressionCode(long hashCode) {

		long val = (COMPRESSION_A * hashCode) + COMPRESSION_B;
		int comValue = (int) (val % capacity);
		if (comValue < 0)
			comValue += capacity;
		return comValue;
	}

	/*
	 * In case of collision find the next slot in table Using Linear probe
	 * technique to resolve collision
	 */
	private int getNextPossibleLinearIndex(int j) {
		return ++j % capacity;
	}

	// In case of collision, find next empty slot
	private int resolveCollision(String studentId) {

		int index = -1;
		int j = 1;
		long hashCode = computeHashCode(studentId);
		int hashIndex = computeCompressionCode(hashCode);
		int index2 = hashIndex;
		/*
		 * In case of collision find next possible empty index. Max no of trials
		 * to find next empty index is equal to size of array.
		 */
		while (j <= capacity) {
			index2 = getNextPossibleLinearIndex(index2);
			if (recordTable[index2] == null) {
				index = index2;
				break;
			}

			j++;
		}
		return index;
	}

	// Insert record in hash table
	public void insert(String studentId, float CGPA) {

		if (recordCount < capacity) {
			if (isStudentIdValid(studentId)) {
				long hashCode = computeHashCode(studentId);
				int hashIndex = computeCompressionCode(hashCode);
				if (resolutionStrategy == CollissionResolutionStrategy.SEPARATE_CHAINING) {
					Node newNode = new Node(hashIndex, new StudentRecord(studentId, CGPA));
					if (recordNodes[hashIndex] == null) {
						recordNodes[hashIndex] = newNode;
						recordCount++;
					} else {
						recordNodes[hashIndex].addNode(newNode);
						recordCount++;
					}
					if (linkedNodes == null) {
						linkedNodes = newNode;
					} else {
						linkedNodes.setNextLinked(newNode);
					}
				} else {
					// insert in table if there is no collision
					if (recordTable[hashIndex] == null) {
						recordTable[hashIndex] = new StudentRecord(studentId, CGPA);
						recordCount++;
					} else {
						// find next possible empty slot
						int newIndex = resolveCollision(studentId);
						if (newIndex == -1) {
							System.out.println("Insertion of " + studentId + "not possible");
						} else {
							recordTable[newIndex] = new StudentRecord(studentId, CGPA);
							recordCount++;
						}
					}
				}
			} else {
				System.out.println("provided student id is not valid, can not insert the record");
			}
		} else {
			System.out.println("Hash table is full");
		}

		if (recordCount / capacity > 0.75) {
			reSizeTable();
		}
	}

	private void reSizeTable() {
		int newCapacity = 2 * capacity;
		StudentHash newHash = new StudentHash();
		newHash.initialize(newCapacity);

		if (resolutionStrategy == CollissionResolutionStrategy.SEPARATE_CHAINING) {
			for (Node node : recordNodes) {
				Node temp = node;
				if (temp != null) {
					do {
						newHash.insert(temp.getRecord().getStudentId(), temp.getRecord().getCgpa());
						temp = temp.next();
					} while (temp != null);
				}

			}
			recordNodes = newHash.recordNodes;
		} else {
			for (StudentRecord stud : recordTable) {
				if (stud != null) {
					newHash.insert(stud.getStudentId(), stud.getCgpa());
				}
			}
			recordTable = newHash.recordTable;
		}
		capacity = newCapacity;
	}

	// Find in hash table the CGPA for provided studentId.
	// In case there is no entry return null.
	public Float get(String studentId) {

		Float CGPA = null;
		if (isStudentIdValid(studentId)) {

			long hashCode = computeHashCode(studentId);
			int hashIndex = computeCompressionCode(hashCode);
			if (studentId == null) {
				System.out.println("studentId is null");
				return null;
			}
			if (resolutionStrategy == CollissionResolutionStrategy.SEPARATE_CHAINING) {
				StudentRecord record = recordNodes[hashIndex].findStudent(studentId);
				if (record == null) {
					return null;
				} else {
					CGPA = record.getCgpa();
				}
			} else {
				if (recordTable[hashIndex] != null && studentId.equals(recordTable[hashIndex].getStudentId())) {
					CGPA = recordTable[hashIndex].getCgpa();
				} else {

					// Find an element, maximum the size of table times, if not
					// found then entry not
					// present.
					int trial = 1;
					int index = hashIndex;
					// Max no of trails is equal to size of array
					while (trial <= capacity) {
						index = getNextPossibleLinearIndex(index);
						if (recordTable[index] == null) {
							System.out.println("recordTable[index] is null (index:" + index + ",trial:" + trial);
							return null;
						} else if (studentId.equals(recordTable[index].getStudentId())) {
							CGPA = recordTable[index].getCgpa();
							break;
						}
						trial++;
					}
				}
			}

		}

		return CGPA;
	}

	// destroy the table
	public void deinitialize() {
		if (resolutionStrategy == CollissionResolutionStrategy.SEPARATE_CHAINING) {
			recordNodes = null;
		} else {
			recordTable = null;
		}

	}

	@Override
	public Iterator<StudentRecord> iterator() {
		// TODO Auto-generated method stub
		if (resolutionStrategy == CollissionResolutionStrategy.SEPARATE_CHAINING) {
			return new StudentHashIterator(linkedNodes);
		} else {
			return new StudentHashIterator(recordTable);
		}
	}
}

// Structure to hold student records
class StudentRecord {

	private String studentId;
	private Float cgpa;

	StudentRecord(String id, Float cgpa) {
		this.studentId = id;
		this.cgpa = cgpa;
	}

	public String getStudentId() {
		return studentId;
	}

	public Float getCgpa() {
		return cgpa;
	}
}

class Node {
	private int hashIndex;
	private StudentRecord record;
	private Node next;
	private Node linkedNext;

	public Node(int hashIndex, StudentRecord record) {
		this.hashIndex = hashIndex;
		this.record = record;
	}

	public StudentRecord getRecord() {
		return record;
	}

	public Node next() {
		return this.next;
	}

	public boolean hasNext() {
		return this.next != null;
	}

	public void addNode(Node node) {
		Node current = this;
		while (current.next != null) {
			current = current.next;
		}
		current.next = node;
	}

	public Node nextLinked() {
		return this.linkedNext;
	}

	public boolean hasNextLinked() {
		return this.linkedNext != null;
	}

	public void setNextLinked(Node node) {
		this.linkedNext = node;
	}

	public StudentRecord findStudent(String studentId) {
		Node current = this;
		while (current.next != null && !current.record.getStudentId().equals(studentId)) {
			current = current.next;
		}
		if (current != null && current.record.getStudentId().equals(studentId)) {
			return current.record;
		}
		return null;
	}
}

class StudentHashIterator implements Iterator<StudentRecord> {

	private int currentIndex;
	private CollissionResolutionStrategy resolutionStrategy;
	private StudentRecord[] recordTable;
	private Node currentNode;

	public StudentHashIterator(StudentRecord[] recordTable) {
		this.recordTable = recordTable;
		this.resolutionStrategy = resolutionStrategy.LINEAR_PROBING;
		this.currentIndex = -1;
	}

	public StudentHashIterator(Node recordNodes) {
		this.currentNode = recordNodes;
		this.resolutionStrategy = resolutionStrategy.SEPARATE_CHAINING;
		this.currentIndex = -1;
	}

	@Override
	public boolean hasNext() {
		if (resolutionStrategy == resolutionStrategy.SEPARATE_CHAINING) {
			return currentNode.hasNextLinked();
		} else {
			int tempIndex = currentIndex + 1;
			while (tempIndex < recordTable.length) {
				if (recordTable[tempIndex++] != null) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public StudentRecord next() {
		if (resolutionStrategy == resolutionStrategy.SEPARATE_CHAINING) {
			currentNode = currentNode.nextLinked();
			return currentNode == null ? null : currentNode.getRecord();
		} else {
			int tempIndex = ++currentIndex;
			while (tempIndex < recordTable.length) {
				StudentRecord record = recordTable[tempIndex];
				if (record != null) {
					currentIndex = tempIndex;
					return record;
				}
				tempIndex++;
			}
		}
		return null;
	}

}