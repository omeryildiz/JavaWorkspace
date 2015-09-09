package tr.gov.tubitak.dokuzeylul.trycatch;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExceptionSample {
	public static void main(String[] args) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		Date date;
		try {
			date = df.parse("2005-10-12");
			System.out.println(date);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

}
