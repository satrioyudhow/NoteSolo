package com.abhijith.note.events;

import com.abhijith.note.model.Note;

public class NoteAddedEvent {
	private final Note note;

	public NoteAddedEvent(Note note) {
		this.note = note;
	}

	public Note getAddedNote() {
		return note;
	}
}
