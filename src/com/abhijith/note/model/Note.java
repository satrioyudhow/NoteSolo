package com.abhijith.note.model;

import java.util.Date;

import com.turbomanage.storm.api.Entity;
import com.turbomanage.storm.api.Persistable;

@Entity
public class Note implements Persistable {

	private long id;
	private String note;
	private Date noteTime;

	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getNoteTime() {
		return noteTime;
	}

	public void setNoteTime(Date noteTime) {
		this.noteTime = noteTime;
		
	}
	@Override
	public String toString() {
		return note;
	}
}
