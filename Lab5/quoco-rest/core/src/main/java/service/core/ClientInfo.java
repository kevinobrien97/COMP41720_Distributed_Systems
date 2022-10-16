package service.core;

import java.io.Serializable;

/**
 * Interface to define the state to be stored in ClientInfo objects
 * 
 * @author Rem
 *
 */
public class ClientInfo implements Serializable {
	
	public static final char MALE				= 'M';
	public static final char FEMALE				= 'F';
	private String name;
	private char gender;
	private int age;
	private int points;
	private int noClaims;
	private String licenseNumber;
	
	public ClientInfo(String name, char sex, int age, int points, int noClaims, String licenseNumber) {
		this.name = name;
		this.gender = sex;
		this.age = age;
		this.points = points;
		this.noClaims = noClaims;
		this.licenseNumber = licenseNumber;
	}
	
	public ClientInfo() {}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setGender(char g) {
		this.gender = g;
	}

	public char getGender() {
		return this.gender;
	}
	public void setAge(int age) {
		this.age = age;
	}

	public int getAge() {
		return this.age;
	}
	public void setPoints(int points) {
		this.points = points;
	}

	public int getPoints() {
		return this.points;
	}
	public void setNoClaims(int years) {
		this.noClaims = years;
	}

	public int getNoClaims() {
		return this.noClaims;
	}
	public void setLicense(String number) {
		this.licenseNumber = number;
	}

	public String getLicenseNumber() {
		return this.licenseNumber;
	}

	/**
	 * Public fields are used as modern best practice argues that use of set/get
	 * methods is unnecessary as (1) set/get makes the field mutable anyway, and
	 * (2) set/get introduces additional method calls, which reduces performance.
	 */

}
