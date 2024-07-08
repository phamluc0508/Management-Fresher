package com.vmo.management_fresher.service.impl;

import com.vmo.management_fresher.base.constant.Constant;
import com.vmo.management_fresher.base.filter.Filter;
import com.vmo.management_fresher.model.*;
import com.vmo.management_fresher.repository.*;
import com.vmo.management_fresher.service.ImportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {
    private final AccountRepo accountRepo;
    private final EmployeeRepo employeeRepo;
    private final CenterRepo centerRepo;
    private final EmployeeCenterRepo employeeCenterRepo;
    private final PositionRepo positionRepo;
    private final PasswordEncoder passwordEncoder;

    private String[] splitFullName(String fullName){
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

        return new String[]{firstName, middleName, lastName};
    }

    @Override
    public Map<String, Object> importFresherToCenter(String uid, MultipartFile file) {
        if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") &&
                !file.getContentType().equals("application/vnd.ms-excel")) {
            throw new RuntimeException("non-excel-file-format");
        }

        List<Employee> employees = new ArrayList<>();
        List<Account> accounts = new ArrayList<>();
        List<EmployeeCenter> employeeCenters = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        List<String> emails = employeeRepo.findAllEmails();
        List<String> phoneNumbers = employeeRepo.findAllPhoneNumbers();

        List<Center> centers = centerRepo.findAll();
        Map<String, Long> centerNamesAndIds = new HashMap<>();
        for(Center center : centers){
            centerNamesAndIds.put(center.getName(), center.getId());
        }

        Position position = positionRepo.findById(Constant.FRESHER_POSITION).orElseThrow(() -> new EntityNotFoundException("position-not-found-with-name: " + Constant.FRESHER_POSITION));

        try {
            Workbook workbook;
            if (file.getContentType().equals("application/vnd.ms-excel")) {
                    workbook = new HSSFWorkbook(file.getInputStream()); // For .xls (HSSF Workbook)
            } else {
                workbook = new XSSFWorkbook(file.getInputStream()); // For .xlsx (XSSF Workbook)
            }

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            Map<String, Integer> columnFields = new HashMap<>();
            for (Cell cell : headerRow) {
                columnFields.put(cell.getStringCellValue(), cell.getColumnIndex());
            }

            int errorColumnIndex = headerRow.getLastCellNum();
            headerRow.createCell(errorColumnIndex).setCellValue("Error");

            int numOfRecord = sheet.getLastRowNum();

            for (int i = 1; i <= numOfRecord; i++) {
                Row row = sheet.getRow(i);
                Employee employee = new Employee();
                EmployeeCenter employeeCenter = new EmployeeCenter();
                Long centerId;

                StringBuilder error = new StringBuilder();
                boolean hasError = false;

                //Read data
                Cell fullNameCell = row.getCell(columnFields.get("Họ tên"));
                Cell emailCell = row.getCell(columnFields.get("Email"));
                Cell phoneNumberCell = row.getCell(columnFields.get("Số điện thoại"));
                Cell centerNameCell = row.getCell(columnFields.get("Trung tâm"));

                if(fullNameCell == null || fullNameCell.getStringCellValue().trim().isEmpty()){
                    error.append("name-cannot-be-empty; ");
                    hasError = true;
                } else {
                    String[] nameParts = splitFullName(fullNameCell.getStringCellValue());
                    employee.setFirstName(nameParts[0]);
                    employee.setMiddleName(nameParts[1]);
                    employee.setLastName(nameParts[2]);
                }

                if (emailCell == null || emailCell.getStringCellValue().trim().isEmpty()) {
                    error.append("email-cannot-be-empty; ");
                    hasError = true;
                } else if(!Pattern.matches(Filter.EMAIL_REGEX, emailCell.getStringCellValue().trim())){
                    error.append("invalid-email-format; ");
                    hasError = true;
                } else {
                    String email = emailCell.getStringCellValue();
                    if(emails.contains(email)){
                        error.append("email-used; ");
                        hasError = true;
                    } else {
                        employee.setEmail(email);
                    }
                }

                if (phoneNumberCell == null || phoneNumberCell.getStringCellValue().trim().isEmpty()) {
                    error.append("phone-number-cannot-be-empty; ");
                    hasError = true;
                } else if(!Pattern.matches(Filter.PHONE_REGEX, phoneNumberCell.getStringCellValue().trim())){
                    error.append("invalid-phone-format; ");
                    hasError = true;
                } else {
                    String phoneNumber = phoneNumberCell.getStringCellValue();
                    if(phoneNumbers.contains(phoneNumber)){
                        error.append("phone-number-used; ");
                        hasError = true;
                    } else {
                        employee.setPhoneNumber(phoneNumber);
                    }
                }

                if(centerNameCell == null || centerNameCell.getStringCellValue().trim().isEmpty()){
                    error.append("center-cannot-be-empty; ");
                    hasError = true;
                } else {
                    String centerName = centerNameCell.getStringCellValue();
                    centerId = centerNamesAndIds.get(centerName);
                    if(centerId == null){
                        error.append("center-does-not-exist; ");
                        hasError = true;
                    } else {
                        employeeCenter.setCenterId(centerId);
                        employeeCenter.setPosition(position);
                        employeeCenter.setCreatedBy(uid);
                        employeeCenter.setUpdatedBy(uid);
                    }
                }

                if(hasError){
                    Cell errorCell = row.createCell(errorColumnIndex);
                    errorCell.setCellValue(error.toString());
                    errors.add(error.toString());
                } else {
                    Account account = new Account();
                    account.setUsername(employee.getEmail());
                    account.setPassword(passwordEncoder.encode(Constant.PASSWORD_DEFAULT));
                    account.setCreatedBy(uid);
                    account.setUpdatedBy(uid);
                    accounts.add(account);

                    employee.setCreatedBy(uid);
                    employee.setUpdatedBy(uid);
                    employees.add(employee);
                    emails.add(employee.getEmail());
                    phoneNumbers.add(employee.getPhoneNumber());

                    employeeCenters.add(employeeCenter);
                }
            }

            Map<String, Object> result = new HashMap<>();

            if(errors.isEmpty()){
                List<Account> accountList = accountRepo.saveAll(accounts);

                for(int i = 0; i < numOfRecord; i++){
                    Account account = accountList.get(i);
                    employees.get(i).setAccountId(account.getId());
                }
                List<Employee> employeeList = employeeRepo.saveAll(employees);

                for(int i = 0; i < numOfRecord; i++){
                    Employee employee = employeeList.get(i);
                    employeeCenters.get(i).setEmployeeId(employee.getId());
                }
                employeeCenterRepo.saveAll(employeeCenters);
                workbook.close();
                return null;
            } else {
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

                result.put("downloadURL", downloadURL);
                return result;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
