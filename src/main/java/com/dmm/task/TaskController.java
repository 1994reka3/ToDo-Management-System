package com.dmm.task;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	public String getTasks(Model model, @AuthenticationPrincipal AccountUserDetails user) {
		if(user.getRoleName().equals("ADMIN")) {
			List<Tasks> tasks = tasksRepo.findAll();  // 管理者ログイン時は全タスク表示
			model.addAttribute("tasks", tasks);
		} else {
			List<Tasks> tasks = tasksRepo.findByName(user.getName());  // ユーザーログイン時はログインユーザーのタスクのみ表示
			model.addAttribute("tasks", tasks);
		}
		return "main";
	}
	
	/**
	 * タスク登録
	 * @param taskForm 送信データ
	 * @return 登録画面
	 */
	@GetMapping("/main/create/{date}")
	public String getNewTask(Model model) {
		TaskForm taskForm = new TaskForm();
		model.addAttribute("taskForm", taskForm);
		return "create";
	}
	
	@PostMapping("/main/create")
	public String create(TaskForm taskForm, @AuthenticationPrincipal AccountUserDetails user) {
		Tasks task = new Tasks();
		task.setName(user.getName());
		task.setTitle(taskForm.getTitle());
		task.setDate(taskForm.getDate());
		task.setText(taskForm.getText());
		task.setDone(false);
		tasksRepo.save(task);
		return "redirect:/main";
	}
	
	
	/**
	 * タスク編集
	 * @param タスクID
	 * @param タスク情報
	 * @return 編集画面
	 */
	@GetMapping("/main/edit/{id}")
	public String edit(@PathVariable Integer id, Model model) {
		Tasks task = tasksRepo.getById(id);
		model.addAttribute("task", task);
		return "edit";
	}
	
	@PostMapping("/main/edit/{id}")
	public String update(@PathVariable Integer id, TaskForm taskForm) {
		Tasks task = tasksRepo.getById(id);
		task.setTitle(taskForm.getTitle());
		task.setDate(taskForm.getDate());
		task.setText(taskForm.getText());
		if (taskForm.getDone() != null) {
			task.setDone(taskForm.getDone());
		} else {
			task.setDone(false);
		}
		tasksRepo.save(task);
		return "redirect:/main";
	}
	
	
	/**
	 * タスク削除
	 *@param id タスクID
	 *@return 遷移先
	 */
	@PostMapping("/main/delete/{id}")
	public String delete(@PathVariable Integer id) {
		tasksRepo.deleteById(id);
		return "redirect:/main";
	}
}

