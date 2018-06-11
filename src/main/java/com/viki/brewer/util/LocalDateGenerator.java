package com.viki.brewer.util;

import java.time.LocalDate;
import java.time.Month;

public class LocalDateGenerator {

	public static void main(String[] args) {
		LocalDate data1 = LocalDate.of(2018, Month.JANUARY, 1);
		System.out.println(data1.toEpochDay() * 24 * 60 * 60);

		LocalDate data2 = LocalDate.of(2018, Month.JUNE, 6);
		System.out.println(data2.toEpochDay() * 24 * 60 * 60);
	}
	
}
