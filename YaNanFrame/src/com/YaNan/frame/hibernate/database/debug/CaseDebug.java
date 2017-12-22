package com.YaNan.frame.hibernate.database.debug;

import com.YaNan.frame.hibernate.database.Case;

public class CaseDebug {
	public static void main(String[] args) {
		String cs = "CASE WHEN salary >= 5000 THEN salary * 0.9 WHEN salary >= 2000 AND salary < 4600 THEN salary * 1.15 ELSE salary END";
		Case c = new Case("salary");
		c.addCaseAnd("salary * 0.9", "salary >= 5000");
		c.addCaseAnd("salary * 1.15","salary >=2000","salary < 4600");
		System.out.println(c.create());// CASE WHEN salary >= 5000  THEN salary * 0.9 WHEN salary >=2000 AND salary < 4600  THEN salary * 1.15 ELSE salary END
	}
}
