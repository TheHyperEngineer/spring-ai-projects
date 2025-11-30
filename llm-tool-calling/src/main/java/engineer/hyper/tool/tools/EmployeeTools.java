package engineer.hyper.tool.tools;

import engineer.hyper.tool.models.Employee;
import engineer.hyper.tool.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeTools {
    private final EmployeeService employeeService;

    @Tool(description = "Get employee details for a given employee id of SivaLabs company")
    public Employee getEmployee(String empId) {
        log.info("Getting employee: {}", empId);
        Employee employee = employeeService.getEmployee(empId);
        log.info("Employee: {}", employee);
        return employee;
    }

    @Tool(description = "Find employees of SivaLabs company who are on leave for a given date in YYYY-MM-DD format")
    public List<Employee> findEmployeesOnLeave(LocalDate date) {
        log.info("Finding employees on leave for date: {}", date);
        List<Employee> employeesOnLeave = employeeService.findEmployeesOnLeave(date);
        log.info("Employees on leave: {} on date {}", employeesOnLeave, date);
        return employeesOnLeave;
    }

    @Tool(description = "Apply leave for a given employee id of SivaLabs company and date in YYYY-MM-DD format")
    public void applyLeave(String empId, LocalDate date) {
        log.info("Applying leave for employee: {} on date: {}", empId, date);
        employeeService.applyLeave(empId, date);
    }
}