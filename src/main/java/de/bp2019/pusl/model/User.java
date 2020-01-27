package de.bp2019.pusl.model;

import java.util.Set;

import com.mongodb.lang.NonNull;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import de.bp2019.pusl.enums.UserType;

/**
 * Model of a User
 * 
 * @author Leon Chemnitz
 */
@Document
public class User {

	@Id
	private ObjectId id;
	
	private String firstName;

	private String lastName;
	
	private String emailAddress;
	
	private String password;

	@DBRef
	private Set<Institute> institutes;
	
	private UserType type;

	public User() {
	}

	public User(String firstName, String lastName, String emailAddress, String password, Set<Institute> institutes,
			UserType type) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.password = password;
		this.institutes = institutes;
		this.type = type;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String eMail) {
		this.emailAddress = eMail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Institute> getInstitutes() {
		return institutes;
	}

	public void setInstitutes(Set<Institute> institutes) {
		this.institutes = institutes;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

}