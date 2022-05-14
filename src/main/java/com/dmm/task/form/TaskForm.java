package com.dmm.task.form;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class TaskForm {
	
	private Integer id;
	
	@Size(min = 1, max = 200)
	private String title;
	
	@Size(min = 1, max = 200)
	private String text;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull
	private LocalDate date;
	
	private Boolean done = Boolean.FALSE;   // 0がfalse, 1がtrue
}
