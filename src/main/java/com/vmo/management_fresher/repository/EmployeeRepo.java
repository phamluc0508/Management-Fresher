package com.vmo.management_fresher.repository;

import com.vmo.management_fresher.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Optional<Employee> findByAccountId(String accountId);

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

    @Query(value = "select DISTINCT e" +
            " from Employee e" +
            " left join EmployeeCenter ec on e.id = ec.employeeId" +
            " left join AssessmentFresher af on e.id = af.employeeId" +
            " left join af.programmingLanguages pl" +
            " where (ec.position.name = :position or coalesce(:position, '#') = '#')" +
            " and (pl.name = :programmingLanguage or coalesce(:programmingLanguage, '#') = '#')" +
            " and (e.email = :email or coalesce(:email, '#') = '#')" +
            " and ((lower(concat(coalesce(e.firstName,''), coalesce(e.middleName,''), coalesce(e.lastName,'') ))" +
            " like lower(concat('%',:name,'%')) or coalesce(:name, '#') = '#'))"
    )
    Page<Employee> searchEmployee(@Param("name") String name, @Param("email") String email,
                                  @Param("position") String position,
                                  @Param("programmingLanguage") String programmingLanguage,
                                  Pageable pageable);

    @Query(value = "select DISTINCT e" +
            " from Employee e" +
            " left join EmployeeCenter ec on e.id = ec.employeeId" +
            " left join AssessmentFresher af on e.id = af.employeeId" +
            " left join af.programmingLanguages pl" +
            " where (ec.position.name = :position or coalesce(:position, '#') = '#')" +
            " and ec.centerId IN (:centerIds)" +
            " and (pl.name = :programmingLanguage or coalesce(:programmingLanguage, '#') = '#')" +
            " and (e.email = :email or coalesce(:email, '#') = '#')" +
            " and ((lower(concat(coalesce(e.firstName,''), coalesce(e.middleName,''), coalesce(e.lastName,'') ))" +
            " like lower(concat('%',:name,'%')) or coalesce(:name, '#') = '#'))"
    )
    Page<Employee> searchEmployeeByCenterIds(@Param("name") String name, @Param("email") String email,
                                  @Param("position") String position,
                                  @Param("programmingLanguage") String programmingLanguage,
                                  @Param("centerIds") List<Long> centerIds,
                                  Pageable pageable);

    @Query(value = "select DISTINCT e" +
            " from Employee e" +
            " inner join EmployeeCenter ec on e.id = ec.employeeId" +
            " where ec.centerId = :centerId" +
            " and ec.position.name = :position")
    List<Employee> findEmployeesInCenter(@Param("centerId") Long centerId, @Param("position") String position);

    @Query(value = "select a.assessmentType as type" +
            " , af.point as point" +
            ", e as employees" +
            " from Employee e" +
            " inner join EmployeeCenter ec on e.id = ec.employeeId" +
            " inner join AssessmentFresher af on e.id = af.employeeId" +
            " inner join Assessment a on a.id = af.assessmentId" +
            " where ec.position.name = :position" +
            " order by a.assessmentType, af.point")
    List<Map<String, Object>> findEmployeesByPoint(@Param("position") String position);

    @Query(value = "select a.assessmentType as type" +
            " , af.point as point" +
            ", e as employees" +
            " from Employee e" +
            " inner join EmployeeCenter ec on e.id = ec.employeeId" +
            " inner join AssessmentFresher af on e.id = af.employeeId" +
            " inner join Assessment a on a.id = af.assessmentId" +
            " where ec.position.name = :position" +
            " and ec.centerId IN (:centerIds)" +
            " order by a.assessmentType, af.point")
    List<Map<String, Object>> findEmployeesByPointAndCenterIds(@Param("centerIds")List<Long> centerIds, @Param("position") String position);

    @Query(value = "select e as employees" +
            " , AVG(af.point) as point" +
            " from Employee e" +
            " inner join EmployeeCenter ec on e.id = ec.employeeId" +
            " inner join AssessmentFresher af on e.id = af.employeeId" +
            " inner join Assessment a on a.id = af.assessmentId" +
            " where ec.position.name = :position" +
            " and a.assessmentType IN (1, 2, 3)" +
            " group by e" +
            " having count(distinct a.assessmentType) = 3" +
            " order by AVG(af.point)")
    List<Map<String, Object>> findEmployeesByAVG(@Param("position") String position);

    @Query(value = "select e as employees" +
            " , AVG(af.point) as point" +
            " from Employee e" +
            " inner join EmployeeCenter ec on e.id = ec.employeeId" +
            " inner join AssessmentFresher af on e.id = af.employeeId" +
            " inner join Assessment a on a.id = af.assessmentId" +
            " where ec.position.name = :position" +
            " and ec.centerId IN (:centerIds)" +
            " and a.assessmentType IN (1, 2, 3)" +
            " group by e" +
            " having count(distinct a.assessmentType) = 3" +
            " order by AVG(af.point)")
    List<Map<String, Object>> findEmployeesByAVGAndCenterIds(@Param("centerIds")List<Long> centerIds, @Param("position") String position);

    @Query(value = "select e.email" +
            " from Employee e" )
    List<String> findAllEmails();

    @Query(value = "select e.phoneNumber" +
            " from Employee e" )
    List<String> findAllPhoneNumbers();
}
