package com.pg.expensetracker.model;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthExpense {
    private String month;
    private String year;
    private Double total;

    public String getMonthName() {
        Map<String, String> map = new HashMap<>();
        map.put("1", "January");
        map.put("2", "February");
        map.put("3", "March");
        map.put("4", "April");
        map.put("5", "May");
        map.put("6", "June");
        map.put("7", "July");
        map.put("8", "August");
        map.put("9", "September");
        map.put("10", "October");
        map.put("11", "November");
        map.put("12", "December");
        return map.containsKey(this.month) ? map.get(this.month) : null;
    }
}
