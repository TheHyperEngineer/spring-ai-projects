package engineer.hyper.tool.services;

import engineer.hyper.tool.models.Employee;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {
    private static final Map<String, Employee> employeeTable = new HashMap<>();
    private static final Map<LocalDate, List<String>> employeeLeavesTable = new HashMap<>();

    @PostConstruct
    void init() {
        employeeTable.put("emp1001", new Employee("emp1001", "John Doe", "john.doe@example.com"));
        employeeTable.put("emp1002", new Employee("emp1002", "Siva", "siva@example.com"));
        employeeTable.put("emp1003", new Employee("emp1003", "James", "james@example.com"));

        employeeLeavesTable.put(LocalDate.now(), List.of("emp1001", "emp1003"));
        employeeLeavesTable.put(LocalDate.of(2025, 1, 1), List.of("emp1001", "emp1002"));
        employeeLeavesTable.put(LocalDate.of(2025, 1, 2), List.of("emp1002", "emp1003"));
    }

    public Employee getEmployee(String empId) {
        return employeeTable.get(empId);
    }

    public List<Employee> findEmployeesOnLeave(LocalDate date) {
        List<String> empIds = employeeLeavesTable.get(date);
        return empIds == null ? List.of() : getEmployees(empIds);
    }

    public List<Employee> getEmployees(List<String> empIds) {
        return empIds.stream().map(employeeTable::get).toList();
    }

    public void applyLeave(String empId, LocalDate date) {
        List<String> empIds = employeeLeavesTable.get(date);
        if (empIds == null) {
            empIds = List.of(empId);
        } else {
            empIds.add(empId);
        }
        employeeLeavesTable.put(date, empIds);
    }
}