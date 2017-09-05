package com.aragost.todo.svc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("todo")
@CrossOrigin  // NB No origin restriction specified!
public class ToDoController {

	private List<ToDo> todos = new ArrayList<>();

	public ToDoController() {
		ToDo t1 = new ToDo(1, "Learn Docker", "Look into learning about containers and Docker.");
		ToDo t2 = new ToDo(2, "Learn Spring Boot", "Learn to use Spring Boot to create REST APIs.");
		ToDo t3 = new ToDo(3, "Learn Angular", "Create Single Page Applications to work with the REST API.");
		t2.setCompleted(true);

		todos.add(t1);
		todos.add(t2);
		todos.add(t3);
	}

	@GetMapping
	public List<ToDo> getAll() {
		synchronized (todos) {
			return new ArrayList<>(todos);
		}
	}

	@PostMapping
	public ToDo save(@RequestBody final ToDo newTodo) {
		int maxId = 1;
		synchronized (todos) {
			for (ToDo t : todos) {
				if (t.getId() > maxId) {
					maxId = t.getId();
				}
			}

			ToDo savedTodo = new ToDo(maxId + 1, newTodo.getTitle(), newTodo.getDescription());
			todos.add(savedTodo);

			return savedTodo;
		}
	}

}
