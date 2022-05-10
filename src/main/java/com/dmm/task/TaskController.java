package com.dmm.task;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
	 * @param date 表示年月日
	 * @param month 表示月
	 * @param matrix カレンダー
	 * @param tasks タスク一覧
	 *
	 */
	@GetMapping("/main")
	public String getTasks(Model model, @AuthenticationPrincipal AccountUserDetails user,
			@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		// カレンダー実装ここから
		if (date == null) {    // 今月の場合
			date = LocalDate.now().withDayOfMonth(1);    // その月の1日を取得
		}
		model.addAttribute("prev", date.minusMonths(1)); // 「＜」で前月取得
		model.addAttribute("next", date.plusMonths(1));  // 「＞」で翌月取得
		DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy年MM月");
		model.addAttribute("month", f.format(date));  // 表示月

		LocalDate firstDay = date.withDayOfMonth(1);        // 表示月の1日
		DayOfWeek firstWeek = firstDay.getDayOfWeek();      // 表示月の1日の曜日
		int firstWeekValue = firstWeek.getValue();          // 表示月の1日の曜日のvalueを取得
		LocalDate day = firstDay.minusDays(firstWeekValue); // 表示月の1日を含む1週目の日にち（日曜日）
		LocalDate lastDay = firstDay.plusDays(firstDay.lengthOfMonth() - 1);   // 表示月の月末日
		DayOfWeek lastWeek = lastDay.getDayOfWeek();        // 表示月の月末日の曜日
		int lastWeekValue = lastWeek.getValue();            // 表示月の月末日の曜日の値
		LocalDate lastSat = lastDay.plusDays(6 - lastWeekValue);   // 表示月の月末日を含む最終週の土曜日
		
		List<List<LocalDate>> matrix = new ArrayList<>(); // 1月のリスト
		while (true) {   // 1月リストの作成ループ
			List<LocalDate> week = new ArrayList<>();     // 1週間のリスト
			while (true) {   // 1週間リストの作成ループ
				week.add(day);
				DayOfWeek strWeek = day.getDayOfWeek();
				LocalDate weekLastDay = week.get(week.size()-1);
				if (strWeek == DayOfWeek.SATURDAY && weekLastDay.isBefore(firstDay) == true) {
					break;  // 土曜日かつ1週間リストの最後の日付が1日より小さい場合は1週間ループを抜ける
				} else if(strWeek == DayOfWeek.SATURDAY) {
					matrix.add(week);    // 土曜日の場合は1週間のリストを1月のリストに追加
					break;  // 1週間ループを抜ける
				}
				day = day.plusDays(1);
			}
			day = day.plusDays(1);
			if (week.contains(lastSat)) {
				break;   // 月末日が1週間リストに含まれていたら1月ループを抜ける
			}
		}
		model.addAttribute("matrix", matrix);
		// カレンダー実装ここまで
		
		// タスク一覧表示
		List<Tasks> task;
		if (user.getRoleName().equals("ADMIN")) {
			task = tasksRepo.findByDateBetweenAdmin(firstDay, lastDay); // 管理者ログイン時は全タスク表示
		} else {
			task = tasksRepo.findByDateBetweenUser(firstDay, lastDay, user.getName()); // ユーザーログイン時はログインユーザーのタスクのみ表示
		}
		
		MultiValueMap<LocalDate, Tasks> tasks = new LinkedMultiValueMap<>();
		for (Tasks t : task) {
			tasks.add(t.getDate(), t);  // key＝タスクの日付 value＝タスクオブジェクト
		}
		model.addAttribute("tasks", tasks);
		return "main";
	}

	/**
	 * タスク登録
	 * @param date 表示年月日
	 * @param taskForm 送信データ
	 * @return 登録画面
	 */
	@GetMapping("/main/create/{date}")
	public String getNewTask(Model model, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		TaskForm taskForm = new TaskForm();
		model.addAttribute("taskForm", taskForm);
		model.addAttribute("date", date);
		return "create";
	}

	@PostMapping("/main/create")
	public String create(TaskForm taskForm, @AuthenticationPrincipal AccountUserDetails user) {
		Tasks task = new Tasks();
		task.setName(user.getName());
		task.setTitle(taskForm.getTitle());
		task.setDate(taskForm.getDate());
		task.setText(taskForm.getText());
		task.setDone(taskForm.getDone());
		tasksRepo.save(task);
		return "redirect:/main";
	}

	/**
	 * タスク編集
	 * @param id タスクID
	 * @param task タスク情報
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
		task.setDone(taskForm.getDone());
		tasksRepo.save(task);
		return "redirect:/main";
	}

	/**
	 * タスク削除
	 * @param id タスクID
	 * @return カレンダー画面
	 */
	@PostMapping("/main/delete/{id}")
	public String delete(@PathVariable Integer id) {
		tasksRepo.deleteById(id);
		return "redirect:/main";
	}
}
