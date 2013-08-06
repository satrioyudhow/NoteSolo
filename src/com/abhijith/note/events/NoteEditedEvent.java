package com.abhijith.note.events;

public class NoteEditedEvent {
	private long mNoteId;

	public NoteEditedEvent(long noteId) {
		mNoteId = noteId;
	}

	public long getNoteId() {
		return mNoteId;
	}

	public void setNoteId(long mNoteId) {
		this.mNoteId = mNoteId;
	}
	
	

}
