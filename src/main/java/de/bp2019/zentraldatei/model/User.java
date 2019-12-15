package de.bp2019.zentraldatei.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import de.bp2019.zentraldatei.enums.UserType;

import java.util.Set;

/**
 * A class to model a useser. Consists of a first and last name, an E-mail
 * adress, a password, a list of institutes the user belongs to, and the user
 * type
 * 
 * @author Alex Späth
 */
@Document
public class User {

	@Id
	private String id;
	private String firstName;
	private String lastName;
	private String eMail;
	private String password;
	/** Foreign Key - Institute.id */
	private Set<String> institutes;
	private UserType type;

	public User(String firstName, String lastName, String eMail, String password, Set<String> institutes,
			UserType type) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.eMail = eMail;
		this.password = password;
		this.institutes = institutes;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEMail() {
		return eMail;
	}

	public void setEMail(String eMail) {
		this.eMail = eMail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<String> getInstitutes() {
		return institutes;
	}

	public void setInstitutes(Set<String> institutes) {
		this.institutes = institutes;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

}