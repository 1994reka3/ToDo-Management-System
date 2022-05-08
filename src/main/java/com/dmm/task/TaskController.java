package com.dmm.task;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.dmm.task.data.entity.Tasks;
import com.dmm.task.data.repository.TasksRepository;
import com.dmm.task.form.TaskForm;
import com.dmm.task.service.AccountUserDetails;

@Controller
public class TaskController {
	
	@Autowired
	private TasksRepository tasksRepo;
	
	/**
	 * タスク一覧（カレンダー）表示
	 *
	 */
	@GetMapping("/main")
	public String getTasks(Model model) {
		List<Tasks> tasks = tasksRepo.findAll();
		model.addAttribute("tasks", tasks);
		return "main";
	}
	
	/**
	 * タスク登録
	 * @param taskForm 送信データ
	 * @return 登録画面へ遷移
	 */
	@GetMapping("/main/create/{date}")
	public String getNewTask(Model model) {
		TaskForm taskForm = new TaskForm();
		model.addAttribute("taskForm", taskForm);
		return "create";
	}
	
	@PostMapping("/main/create")
	public String create(TaskForm taskForm, @AuthenticationPrincipal AccountUserDetails user) {
		Tasks tasks = new Tasks();
		tasks.setName(user.getName());
		tasks.setTitle(taskForm.getTitle());
		tasks.setDate(taskForm.getDate());
		tasks.setText(taskForm.getText());
		tasks.setDone(false);
		tasksRepo.save(tasks);
		return "redirect:/main";
	}
}

