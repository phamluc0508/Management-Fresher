package com.vmo.management_fresher.repository;

import com.vmo.management_fresher.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    Boolean existsByEmailAndIdIsNot(String email, Long id);
    Boolean existsByPhoneNumberAndIdIsNot(String phoneNumber, Long id);

    Boolean existsByEmail(String email);
    Boolean existsByPhoneNumber(String phoneNumber);

    @Query(value = "select e.id as id" +
            " , e.firstName as fistName" +
            " , e.middleName as middleName" +
            " , e.lastName as lastName" +
            " , e.email as email" +
            " , e.phoneNumber as phoneNumber" +
            " , a.username as username" +
            " from Employee e " +
            " left join Account a on a.id = e.accountId" +
            " where e.id = :id"
    )
    Optional<Map<String, Object>> getEmployeeById(@Param("id")Long id);

    @Query(value = "select e.id as id" +
            " , e.firstName as fistName" +
            " , e.middleName as middleName" +
            " , e.lastName as lastName" +
            " , e.email as email" +
            " , e.phoneNumber as phoneNumber" +
            " , a.username as username" +
            " from Employee e " +
            " left join Account a on a.id = e.accountId"
    )
    List<Map<String, Object>> getAll();
}
