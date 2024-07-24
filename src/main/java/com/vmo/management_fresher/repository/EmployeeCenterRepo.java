package com.vmo.management_fresher.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vmo.management_fresher.model.EmployeeCenter;

@Repository
public interface EmployeeCenterRepo extends JpaRepository<EmployeeCenter, Long> {
    Optional<EmployeeCenter> findByEmployeeIdAndCenterIdAndPositionName(
            Long employeeId, Long centerId, String positionName);

    Optional<EmployeeCenter> findByEmployeeIdAndCenterId(Long employeeId, Long centerId);

    Optional<EmployeeCenter> findByEmployeeIdAndPositionName(Long employeeId, String positionName);

    Boolean existsByEmployeeId(Long employeeId);

    Boolean existsByEmployeeIdAndIdIsNot(Long employeeId, Long id);

    Boolean existsByEmployeeIdAndPositionName(Long employeeId, String positionName);

    Boolean existsByCenterId(Long centerId);

    List<EmployeeCenter> findAllByCenterIdIn(List<Long> centerIds);

    List<EmployeeCenter> findAllByEmployeeIdAndPositionName(Long employeeId, String positionName);

    Boolean existsByEmployeeIdAndCenterIdAndPositionName(Long employeeId, Long centerId, String positionName);

    @Query(
            value = "select ec.position.name as position" + " , c.name as centerName"
                    + " from EmployeeCenter ec"
                    + " inner join Employee e on e.id = ec.employeeId"
                    + " inner join Center c on c.id = ec.centerId"
                    + " where e.id = :employeeId")
    List<Map<String, Object>> getByEmployeeId(@Param("employeeId") Long employeeId);

    @Query(
            value = "select ec.centerId" + " from EmployeeCenter ec"
                    + " where ec.employeeId = :directorId"
                    + " and ec.position.name = :positionName")
    List<Long> findCenterIdsByDirectorId(
            @Param("directorId") Long directorId, @Param("positionName") String positionName);
}
