package de.bp2019.zentraldatei.model;

import com.mongodb.lang.NonNull;

import org.springframework.data.mongodb.core.mapping.DBRef;

/**
 * A class to model a instance of an exercise. Always an embedded Document in a Module.
 * 
 * @author Leon Chemnitz
 */
public class Exercise {

	@NonNull
	private String name;

	@DBRef
	private ExerciseScheme scheme;

	@NonNull
	private boolean assignableByHIWI;

	public Exercise(String name, ExerciseScheme scheme, boolean assignableByHIWI) {
		this.name = name;
		this.scheme = scheme;
		this.assignableByHIWI = assignableByHIWI;
	}

	public Exercise() {
	}

	public ExerciseScheme getScheme() {
		return scheme;
	}

	public void setScheme(ExerciseScheme scheme) {
		this.scheme = scheme;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAssignableByHIWI() {
		return assignableByHIWI;
	}

	public void setAssignableByHIWI(boolean assignableByHIWI) {
		this.assignableByHIWI = assignableByHIWI;
	}

}