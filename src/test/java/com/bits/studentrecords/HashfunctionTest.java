package com.bits.studentrecords;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class HashfunctionTest {

	private Map<String, Float> hashMap;
	private StudentHash hash;
	private String[] courses = { "CSE", "MEC", "ARC", "ECE" };

	@Before
	public void setup() {
		hashMap = new HashMap<String, Float>();
		hash = new StudentHash();
		hash.initialize();
		int count = 0;
		for (int i = 2016; i < 2019; i++) {
			for (String course : courses) {
				for (int j = 10001; j < 20000; j++) {
					int roll = j - 10000;
					String id = i + course;
					if (roll < 10) {
						id = id.concat("000");
					} else if (roll < 100) {
						id = id.concat("00");
					} else if (roll < 1000) {
						id = id.concat("0");
					}
					id = id.concat(String.valueOf(roll));

					Random rand = new Random();
					float val = rand.nextFloat();
					hash.insert(id, val);
					hashMap.put(id, val);
					count++;
				}
			}
		}
		// System.out.println(count);
	}

	@Test
	public void testCollisions() {
		hashMap.forEach((key, value) -> {
			assertEquals(value, hash.get(key));
		});
	}

	@Test
	public void testIteration() {
		hashMap.forEach((key, value) -> {
			assertEquals(value, hash.get(key));
		});
		Iterator<StudentRecord> iterator = hash.iterator();
		int count = 0;
		while (iterator.hasNext()) {
			StudentRecord record = iterator.next();
			assertEquals(record.getCgpa(), hashMap.get(record.getStudentId()));
			count++;
		}
		assertEquals(hash.size(), count);

	}
}
