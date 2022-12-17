package com.pg.expensetracker.repository;

import com.pg.expensetracker.model.Expense;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepo extends JpaRepository<Expense, Long> {
    public List<Expense> findByUserId(Long userId, Pageable pageable);

    public List<Expense> findAllByDateBetween(Date startDate, Date endDate);

    @Query("select sum(e.amount) from Expense e where e.date BETWEEN :startDate AND :endDate")
    public Optional<Double> findSumAmountBetweenDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
