package com.vmo.management_fresher.service.impl;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.vmo.management_fresher.base.constant.Constant;
import com.vmo.management_fresher.base.validate.Validate;
import com.vmo.management_fresher.model.*;
import com.vmo.management_fresher.repository.*;
import com.vmo.management_fresher.service.ImportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {
    private final AccountRepo accountRepo;
    private final EmployeeRepo employeeRepo;
    private final CenterRepo centerRepo;
    private final EmployeeCenterRepo employeeCenterRepo;
    private final PositionRepo positionRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> importFresherToCenter(String uid, MultipartFile file) {
        if (!isExcelFile(file)) {
            throw new IllegalArgumentException("non-excel-file-format");
        }

        List<Employee> employees = new ArrayList<>();
        List<Account> accounts = new ArrayList<>();
        List<EmployeeCenter> employeeCenters = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        Set<String> emails = new HashSet<>(employeeRepo.findAllEmails());
        Set<String> phoneNumbers = new HashSet<>(employeeRepo.findAllPhoneNumbers());

        Map<String, Long> centerNamesAndIds =
                centerRepo.findAll().stream().collect(Collectors.toMap(Center::getName, Center::getId));

        Position position = positionRepo
                .findById(Constant.FRESHER_POSITION)
                .orElseThrow(() ->
                        new EntityNotFoundException("position-not-found-with-name: " + Constant.FRESHER_POSITION));
        Role role = roleRepo.findById(Constant.FRESHER_ROLE)
                .orElseThrow(() -> new EntityNotFoundException("role-not-found-with: " + Constant.FRESHER_ROLE));

        try (Workbook workbook = createWorkbook(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            Map<String, Integer> columnFields = createColumnFieldsMap(headerRow);

            int errorColumnIndex = headerRow.getLastCellNum();
            headerRow.createCell(errorColumnIndex).setCellValue("Error");

            int numOfRecord = sheet.getLastRowNum();

            for (int i = 1; i <= numOfRecord; i++) {
                processRow(
                        sheet.getRow(i),
                        columnFields,
                        errorColumnIndex,
                        employees,
                        accounts,
                        employeeCenters,
                        errors,
                        emails,
                        phoneNumbers,
                        centerNamesAndIds,
                        position,
                        role,
                        uid);
            }

            if (errors.isEmpty()) {
                saveData(employees, accounts, employeeCenters, numOfRecord);
                return null;
            } else {
                return createErrorFile(workbook);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing file", e);
        }
    }

    private boolean isExcelFile(MultipartFile file) {
        String contentType = file.getContentType();
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)
                || "application/vnd.ms-excel".equals(contentType);
    }

    private Workbook createWorkbook(MultipartFile file) throws IOException {
        if ("application/vnd.ms-excel".equals(file.getContentType())) {
            return new HSSFWorkbook(file.getInputStream());
        } else {
            return new XSSFWorkbook(file.getInputStream());
        }
    }

    private Map<String, Integer> createColumnFieldsMap(Row headerRow) {
        Map<String, Integer> columnFields = new HashMap<>();
        for (Cell cell : headerRow) {
            columnFields.put(cell.getStringCellValue(), cell.getColumnIndex());
        }
        return columnFields;
    }

    private void processRow(
            Row row,
            Map<String, Integer> columnFields,
            int errorColumnIndex,
            List<Employee> employees,
            List<Account> accounts,
            List<EmployeeCenter> employeeCenters,
            List<String> errors,
            Set<String> emails,
            Set<String> phoneNumbers,
            Map<String, Long> centerNamesAndIds,
            Position position,
            Role role,
            String uid) {
        Employee employee = new Employee();
        EmployeeCenter employeeCenter = new EmployeeCenter();
        StringBuilder error = new StringBuilder();
        boolean hasError = false;

        hasError |= processName(row, columnFields, employee, error);
        hasError |= processEmail(row, columnFields, employee, error, emails);
        hasError |= processPhoneNumber(row, columnFields, employee, error, phoneNumbers);
        hasError |= processCenter(row, columnFields, employeeCenter, error, centerNamesAndIds, position, uid);

        if (hasError) {
            Cell errorCell = row.createCell(errorColumnIndex);
            errorCell.setCellValue(error.toString());
            errors.add(error.toString());
        } else {
            createAccount(employee, accounts, role, uid);
            employees.add(employee);
            emails.add(employee.getEmail());
            phoneNumbers.add(employee.getPhoneNumber());
            employeeCenters.add(employeeCenter);
        }
    }

    private boolean processName(Row row, Map<String, Integer> columnFields, Employee employee, StringBuilder error) {
        Cell fullNameCell = row.getCell(columnFields.get("Họ tên"));
        if (fullNameCell == null || fullNameCell.getStringCellValue().trim().isEmpty()) {
            error.append("name-cannot-be-empty; ");
            return true;
        } else {
            String[] nameParts = splitFullName(fullNameCell.getStringCellValue());
            employee.setFirstName(nameParts[0]);
            employee.setMiddleName(nameParts[1]);
            employee.setLastName(nameParts[2]);
            return false;
        }
    }

    private boolean processEmail(
            Row row, Map<String, Integer> columnFields, Employee employee, StringBuilder error, Set<String> emails) {
        Cell emailCell = row.getCell(columnFields.get("Email"));
        if (emailCell == null || emailCell.getStringCellValue().trim().isEmpty()) {
            error.append("email-cannot-be-empty; ");
            return true;
        } else if (!Pattern.matches(
                Validate.EMAIL_REGEX, emailCell.getStringCellValue().trim())) {
            error.append("invalid-email-format; ");
            return true;
        } else {
            String email = emailCell.getStringCellValue();
            if (emails.contains(email)) {
                error.append("email-used; ");
                return true;
            } else {
                employee.setEmail(email);
                return false;
            }
        }
    }

    private boolean processPhoneNumber(
            Row row,
            Map<String, Integer> columnFields,
            Employee employee,
            StringBuilder error,
            Set<String> phoneNumbers) {
        Cell phoneNumberCell = row.getCell(columnFields.get("Số điện thoại"));
        if (phoneNumberCell == null
                || phoneNumberCell.getStringCellValue().trim().isEmpty()) {
            error.append("phone-number-cannot-be-empty; ");
            return true;
        } else if (!Pattern.matches(
                Validate.PHONE_REGEX, phoneNumberCell.getStringCellValue().trim())) {
            error.append("invalid-phone-format; ");
            return true;
        } else {
            String phoneNumber = phoneNumberCell.getStringCellValue();
            if (phoneNumbers.contains(phoneNumber)) {
                error.append("phone-number-used; ");
                return true;
            } else {
                employee.setPhoneNumber(phoneNumber);
                return false;
            }
        }
    }

    private boolean processCenter(
            Row row,
            Map<String, Integer> columnFields,
            EmployeeCenter employeeCenter,
            StringBuilder error,
            Map<String, Long> centerNamesAndIds,
            Position position,
            String uid) {
        Cell centerNameCell = row.getCell(columnFields.get("Trung tâm"));
        if (centerNameCell == null || centerNameCell.getStringCellValue().trim().isEmpty()) {
            error.append("center-cannot-be-empty; ");
            return true;
        } else {
            String centerName = centerNameCell.getStringCellValue();
            Long centerId = centerNamesAndIds.get(centerName);
            if (centerId == null) {
                error.append("center-does-not-exist; ");
                return true;
            } else {
                employeeCenter.setCenterId(centerId);
                employeeCenter.setPosition(position);
                employeeCenter.setCreatedBy(uid);
                employeeCenter.setUpdatedBy(uid);
                return false;
            }
        }
    }

    private void createAccount(Employee employee, List<Account> accounts, Role role, String uid) {
        Account account = new Account();
        account.setUsername(employee.getEmail());
        account.setPassword(passwordEncoder.encode(employee.getPhoneNumber()));
        account.setRole(role);
        account.setCreatedBy(uid);
        account.setUpdatedBy(uid);
        accounts.add(account);

        employee.setCreatedBy(uid);
        employee.setUpdatedBy(uid);
    }

    private String[] splitFullName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        String firstName = "";
        String middleName = "";
        String lastName = "";

        if (parts.length == 1) {
            lastName = parts[0];
        } else if (parts.length == 2) {
            firstName = parts[0];
            lastName = parts[1];
        } else {
            firstName = parts[0];
            lastName = parts[parts.length - 1];
            middleName = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length - 1));
        }

        return new String[] {firstName, middleName, lastName};
    }

    private void saveData(
            List<Employee> employees, List<Account> accounts, List<EmployeeCenter> employeeCenters, int numOfRecord) {
        List<Account> accountList = accountRepo.saveAll(accounts);

        for (int i = 0; i < numOfRecord; i++) {
            employees.get(i).setAccountId(accountList.get(i).getId());
        }
        List<Employee> employeeList = employeeRepo.saveAll(employees);

        for (int i = 0; i < numOfRecord; i++) {
            employeeCenters.get(i).setEmployeeId(employeeList.get(i).getId());
        }
        employeeCenterRepo.saveAll(employeeCenters);
    }

    private Map<String, Object> createErrorFile(Workbook workbook) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        // Save the error file to a temporary location
        File tempFile = File.createTempFile("freshers_with_errors", ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(outputStream.toByteArray());
        }

        String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/import/download-error-file/")
                .path(tempFile.getName())
                .toUriString();

        Map<String, Object> result = new HashMap<>();
        result.put("downloadURL", downloadURL);
        return result;
    }
}
