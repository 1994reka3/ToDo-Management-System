package com.dmm.task;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TodoController {
	@GetMapping("/main")
	public String getCalendars() {
		return "main";
	}
}
