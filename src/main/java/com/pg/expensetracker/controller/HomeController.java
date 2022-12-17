package com.pg.expensetracker.controller;

import com.pg.expensetracker.model.Expense;
import com.pg.expensetracker.model.MonthExpense;
import com.pg.expensetracker.model.UserObj;
import com.pg.expensetracker.repository.ExpenseRepo;
import com.pg.expensetracker.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.Cacheable;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Controller
public class HomeController {

    Logger log = LoggerFactory.getLogger(HomeController.class);

    private UserRepo userRepo;
    private ExpenseRepo expenseRepo;

    @Autowired
    public HomeController(UserRepo userRepo, ExpenseRepo expenseRepo) {
        this.userRepo = userRepo;
        this.expenseRepo = expenseRepo;
    }

    @GetMapping("/")
    public String getHomePage() {
        return "index";
    }

    @GetMapping("/signup")
    public String getSignUpPage(Model model, Principal principal) {

        if (principal != null && ((Authentication) principal).isAuthenticated()) {
            return "redirect:/";
        }

        model.addAttribute("user", new UserObj());
        List<String> roles = Arrays.asList("Default", "USER", "ADMIN");
        model.addAttribute("roles", roles);
        return "signup";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") UserObj user) {
        Optional<UserObj> userObj = userRepo.findByName(user.getName());
        if (userObj.isPresent()) {
            return "redirect:/signup?exist";
        }
        userRepo.save(user);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String doSignin(Principal principal) {
        if (principal != null && ((Authentication) principal).isAuthenticated()) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/expense")
    public String getExpensePage(Model model, Principal principal) {
        model.addAttribute("expense", new Expense());

        UserObj user = getUserFromPrincipal(principal);
        if (user != null) {
            Pageable topTen = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
            List<Expense> expenseList = expenseRepo.findByUserId(user.getId(), topTen);
            model.addAttribute("expenselist", expenseList);
        } else {
            log.error("Error while getting expense as no user found from principal");
        }

        return "expense";
    }

    @PostMapping("/submit/expense")
    public String saveExpense(@ModelAttribute("expense") Expense expense, Principal principal) {
        UserObj user = getUserFromPrincipal(principal);
        if (user != null) {
            expense.setUser(user);
            List<Expense> userExpenseList = user.getExpenses();
            userExpenseList.add(expense);
            expenseRepo.save(expense);
            userRepo.save(user);
        } else {
            log.error("Error while saving expense as no user found from principal");
        }

        return "redirect:/expense";
    }

    private UserObj getUserFromPrincipal(Principal principal) {
        String userName = principal.getName();
        Optional<UserObj> userOpt = userRepo.findByName(userName);
        if (userOpt.isPresent()) {
            return userOpt.get();
        }
        return null;
    }

    @GetMapping("/expense/monthly")
    public String getMonthlyExpensePage(@RequestParam(name = "month", defaultValue = "null") String month, @RequestParam(name = "year", defaultValue = "null") String year, Model model) {
        model.addAttribute("monthList", getMonthList());

        if (model.getAttribute("monthExpenseData") == null) {
            model.addAttribute("monthExpenseData", false);
        }

        if (!"null".equals(month) && !"null".equals(year)) {
            log.info("Month : " + month + " Year : " + year);
            try {
                Date[] dates = getStartAndEndDate(month, year);
                if (dates != null && dates.length != 0) {
                    List<Expense> list = expenseRepo.findAllByDateBetween(dates[0], dates[1]);
                    model.addAttribute("monthExpenseList", list);

                    MonthExpense monthExpense = new MonthExpense();
                    monthExpense.setMonth(month);
                    monthExpense.setYear(year);
                    Optional<Double> totalOpt = expenseRepo.findSumAmountBetweenDate(dates[0], dates[1]);
                    if (totalOpt.isPresent()) {
                        log.info("Total Sum is " + totalOpt.get());
                        monthExpense.setTotal(totalOpt.get());
                    }
                    model.addAttribute("monthExpense", monthExpense);
                }
            } catch (Exception e) {
                log.error("Failed to get monthly expenses based on month " + month + " and year " + year, e);
            }
        }
        return "monthlyexpense";
    }

    private List<Pair> getMonthList() {
        List<Pair> monthList = new LinkedList<>();
        monthList.add(Pair.of(1, "January"));
        monthList.add(Pair.of(2, "February"));
        monthList.add(Pair.of(3, "March"));
        monthList.add(Pair.of(4, "April"));
        monthList.add(Pair.of(5, "May"));
        monthList.add(Pair.of(6, "June"));
        monthList.add(Pair.of(7, "July"));
        monthList.add(Pair.of(8, "August"));
        monthList.add(Pair.of(9, "September"));
        monthList.add(Pair.of(10, "October"));
        monthList.add(Pair.of(11, "November"));
        monthList.add(Pair.of(12, "December"));
        return monthList;
    }

    private Date[] getStartAndEndDate(String month, String year) throws ParseException {
        if (LocalDate.now().getYear() < Integer.parseInt(year)) {
            log.error("Provided year is greater than current year");
            return new Date[]{};
        }
        Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(year + "-" + month + "-1");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        Date endDate = calendar.getTime();
        log.info("Start Date : " + startDate.toString());
        log.info("End Date : " + endDate.toString());

        return new Date[]{startDate, endDate};
    }
}
