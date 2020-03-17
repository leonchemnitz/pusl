package de.bp2019.pusl.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A class to model a single grade entry
 * 
 * @author Leon Chemnitz
 */
@Document
public class Grade {
	@Id
	private ObjectId id;

	@DBRef
	private Lecture lecture;

	private Exercise exercise;

	@Indexed(unique = false)
	private String matrNumber;

	@DBRef
	private User gradedBy;

	/** Grade is stored as a string to enable non-numeric entries */
	private String grade;

	private LocalDate handIn;

	private LocalDateTime lastModified;

	public Grade() {
	}

	public Grade(Lecture lecture, Exercise exercise, String matrNumber, String grade, LocalDate handIn) {
		this.lecture = lecture;
		this.exercise = exercise;
		this.matrNumber = matrNumber;
		this.grade = grade;
		this.handIn = handIn;

		lastModified = LocalDateTime.now();
	}

	public void setLecture(Lecture lecture){
		this.lecture = lecture;
	}

	public Lecture getLecture(){
		return lecture;
	}

	public String getMatrNumber() {
		return matrNumber;
	}

	public void setMatrNumber(String matrNumber) {
		this.matrNumber = matrNumber;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public LocalDate getHandIn() {
		return handIn;
	}

	public void setHandIn(LocalDate handIn) {
		this.handIn = handIn;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public User getGradedBy() {
		return gradedBy;
	}

	public void setGradedBy(User gradedBy) {
		this.gradedBy = gradedBy;
	}

	public Exercise getExercise() {
		return exercise;
	}

	public void setExercise(Exercise exercise) {
		this.exercise = exercise;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).toHashCode();
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o,
				Arrays.asList("lecture", "exercise", "matrNumber", "gradedBy", "grade", "handIn", "lastModified"));
	}

}