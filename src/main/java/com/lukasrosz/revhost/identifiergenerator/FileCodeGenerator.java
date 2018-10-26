package com.lukasrosz.revhost.identifiergenerator;

import java.io.Serializable;

import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class FileCodeGenerator implements IdentifierGenerator {

	@Override
	public Serializable generate(SharedSessionContractImplementor arg0, Object arg1) throws HibernateException {
		int length = 10;
	    boolean useLetters = true;
	    boolean useNumbers = true;
	    String generatedCode = RandomStringUtils.random(length, useLetters, useNumbers);
	 
	    return generatedCode;
	}
	

}
