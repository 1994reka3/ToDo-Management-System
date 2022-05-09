package com.dmm.task.data.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dmm.task.data.entity.Tasks;

public interface TasksRepository extends JpaRepository<Tasks, Integer>{
	
	/**
	 * ユーザー名で検索
	 */
	List<Tasks> findByName(String name); // 全タスク取得しているので日付のソートが必要
	
	// ログインユーザーのタスク一覧取得
	@Query("select a from Tasks a where a.date between :from and :to and name = :name")
	List<Tasks> findByDateBetweenUser(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, @Param("name") String name);
	// 管理者用のタスク一覧取得
	@Query("select a from Tasks a where a.date between :from and :to")
	List<Tasks> findByDateBetweenAdmin(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
