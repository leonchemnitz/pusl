package de.bp2019.pusl.model;

import java.time.Instant;

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
	private Lecture lectureRef;

	private Lecture lectureEmb;

	private Exercise exercise;

	@Indexed(unique = false)
	private String matrNumber;

	@DBRef
	private User gradedBy;

	/** Grade is stored as a string to enable non-numeric entries */
	private String grade;

	private Instant handIn;

	public Grade() {
	}

	public Grade(Lecture lecture, Exercise exercise, String matrNumber, String grade, Instant handIn) {
		this.lectureRef = lecture;
		this.lectureEmb = lecture;
		this.exercise = exercise;
		this.matrNumber = matrNumber;
		this.grade = grade;
		this.handIn = handIn;
	}

	public void setLecture(Lecture lecture){
		this.lectureEmb = lecture;
		this.lectureRef = lecture;
	}

	public Lecture getLecture(){
		if(lectureRef != null){
			return lectureRef;
		}else{
			return lectureEmb;
		}
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

	public Instant getHandIn() {
		return handIn;
	}

	public void setHandIn(Instant handIn) {
		this.handIn = handIn;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Lecture getLectureRef() {
		return lectureRef;
	}

	public void setLectureRef(Lecture lectureRef) {
		this.lectureRef = lectureRef;
	}

	public Lecture getLectureEmb() {
		return lectureEmb;
	}

	public void setLectureEmb(Lecture lectureEmb) {
		this.lectureEmb = lectureEmb;
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

	@Override
	public String toString() {
		return "Grade [exercise=" + exercise + ", grade=" + grade + ", gradedBy=" + gradedBy + ", handIn="
				+ handIn + ", lectureEmb=" + lectureEmb + ", matrNumber=" + matrNumber + "]";
	}
}