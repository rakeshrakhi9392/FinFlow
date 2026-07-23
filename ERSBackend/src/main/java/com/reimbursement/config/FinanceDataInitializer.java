package com.reimbursement.config;

import com.reimbursement.entity.Budget;
import com.reimbursement.entity.Department;
import com.reimbursement.entity.ExpenseCategory;
import com.reimbursement.entity.User;
import com.reimbursement.enums.FiscalQuarter;
import com.reimbursement.repository.BudgetRepository;
import com.reimbursement.repository.DepartmentRepository;
import com.reimbursement.repository.ExpenseCategoryRepository;
import com.reimbursement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Seeds departments, categories, budgets, and demo users for local/demo environments.
 * Disabled for {@code prod} and {@code test} profiles — production must provision data explicitly.
 */
@Component
@Profile("!test & !prod")
public class FinanceDataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public FinanceDataInitializer(DepartmentRepository departmentRepository,
                                  ExpenseCategoryRepository expenseCategoryRepository,
                                  BudgetRepository budgetRepository,
                                  UserRepository userRepository,
                                  PasswordEncoder passwordEncoder) {
        this.departmentRepository = departmentRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (departmentRepository.count() > 0) {
            seedDemoUsersIfMissing();
            return;
        }

        Department engineering = departmentRepository.save(new Department("Engineering", "ENG"));
        Department finance = departmentRepository.save(new Department("Finance", "FIN"));
        Department sales = departmentRepository.save(new Department("Sales", "SAL"));
        Department operations = departmentRepository.save(new Department("Operations", "OPS"));

        expenseCategoryRepository.save(new ExpenseCategory("General", "GENERAL", "Uncategorized business expense"));
        expenseCategoryRepository.save(new ExpenseCategory("Travel", "TRAVEL", "Airfare, lodging, ground transport"));
        expenseCategoryRepository.save(new ExpenseCategory("Meals", "MEALS", "Client and team meals"));
        expenseCategoryRepository.save(new ExpenseCategory("Equipment", "EQUIP", "Hardware and peripherals"));
        expenseCategoryRepository.save(new ExpenseCategory("Training", "TRAIN", "Courses and certifications"));

        LocalDate today = LocalDate.now();
        int year = FiscalQuarter.fiscalYearOf(today);
        FiscalQuarter quarter = FiscalQuarter.fromDate(today);

        List.of(
                new Budget(engineering, year, quarter, 50000),
                new Budget(finance, year, quarter, 30000),
                new Budget(sales, year, quarter, 40000),
                new Budget(operations, year, quarter, 25000)
        ).forEach(budgetRepository::save);

        seedDemoUsers(engineering, finance);
    }

    private void seedDemoUsersIfMissing() {
        Department engineering = departmentRepository.findByCode("ENG").orElse(null);
        Department finance = departmentRepository.findByCode("FIN").orElse(null);
        if (engineering != null && finance != null) {
            seedDemoUsers(engineering, finance);
        }
    }

    private void seedDemoUsers(Department engineering, Department finance) {
        seedUser("employee1", "employee", "Alex", "Employee", "employee1@company.com", engineering);
        seedUser("manager1", "manager", "Morgan", "Manager", "manager1@company.com", engineering);
        seedUser("senior1", "senior_manager", "Sam", "Senior", "senior1@company.com", engineering);
        seedUser("finance1", "finance", "Frankie", "Finance", "finance1@company.com", finance);
        seedUser("admin1", "admin", "Avery", "Admin", "admin1@company.com", finance);
    }

    private void seedUser(String username, String role, String first, String last, String email, Department dept) {
        if (userRepository.existsByUsername(username)) {
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(role);
        user.setFirstName(first);
        user.setLastName(last);
        user.setEmail(email);
        user.setDepartment(dept);
        userRepository.save(user);
    }
}
