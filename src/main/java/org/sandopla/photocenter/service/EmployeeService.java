//package org.sandopla.photocenter.service;
//
//import org.sandopla.photocenter.model.Employee;
//import org.sandopla.photocenter.repository.EmployeeRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class EmployeeService {
//
//    private final EmployeeRepository employeeRepository;
//
//    @Autowired
//    public EmployeeService(EmployeeRepository employeeRepository) {
//        this.employeeRepository = employeeRepository;
//    }
//
//    public List<Employee> getAllEmployees() {
//        return employeeRepository.findAll();
//    }
//
//    public Employee getEmployeeById(Long id) {
//        return employeeRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
//    }
//
//    public Employee createEmployee(Employee employee) {
//        return employeeRepository.save(employee);
//    }
//
//    public Employee updateEmployee(Long id, Employee employeeDetails) {
//        Employee employee = getEmployeeById(id);
//        employee.setName(employeeDetails.getName());
//        employee.setPosition(employeeDetails.getPosition());
//        employee.setBranch(employeeDetails.getBranch());
//        employee.setPhoneNumber(employeeDetails.getPhoneNumber());
//        employee.setEmail(employeeDetails.getEmail());
//        employee.setHireDate(employeeDetails.getHireDate());
//        return employeeRepository.save(employee);
//    }
//
//    public void deleteEmployee(Long id) {
//        employeeRepository.deleteById(id);
//    }
//}
