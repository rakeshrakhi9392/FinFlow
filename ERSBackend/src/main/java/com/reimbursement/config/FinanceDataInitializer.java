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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Seeds enterprise finance reference data for local demos.
 */
@Component
@Profile("!test")
public class FinanceDataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public FinanceDataInitializer(DepartmentRepository departmentRepository,
                                  ExpenseCategoryRepository expenseCategoryRepository,
                                  BudgetRepository budgetRepository,
                                  UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (departmentRepository.count() > 0) {
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

    private void seedDemoUsers(Department engineering, Department finance) {
        if (!userRepository.existsByUsername("employee1")) {
            User employee = new User();
            employee.setUsername("employee1");
            employee.setPassword("password");
            employee.setRole("employee");
            employee.setFirstName("Alex");
            employee.setLastName("Employee");
            employee.setEmail("employee1@company.com");
            employee.setDepartment(engineering);
            userRepository.save(employee);
        }
        if (!userRepository.existsByUsername("manager1")) {
            User manager = new User();
            manager.setUsername("manager1");
            manager.setPassword("password");
            manager.setRole("manager");
            manager.setFirstName("Morgan");
            manager.setLastName("Manager");
            manager.setEmail("manager1@company.com");
            manager.setDepartment(finance);
            userRepository.save(manager);
        }
    }
}
