package com.aragost.todo.svc;

public class ToDo {

	private int id;
	private String title;
	private String description;
	private boolean completed;

	public ToDo() {
	}

	public ToDo(final int id, final String title, final String description) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.completed = false;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(final boolean completed) {
		this.completed = completed;
	}

}
