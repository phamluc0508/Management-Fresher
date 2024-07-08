package com.vmo.management_fresher.repository;

import com.vmo.management_fresher.model.Center;
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
public interface CenterRepo extends JpaRepository<Center, Long> {

    Boolean existsByParentId(Long id);

    List<Center> findAllByParentIdIn(List<Long> parentIds);

    Boolean existsByName(String name);

    Boolean existsByNameAndIdIsNot(String name, Long id);

    @Query(value = "select c" +
            " from Center c" +
            " where (lower(coalesce(c.name,'')) like lower(concat('%',:search,'%')) or coalesce(:search, '#') = '#')"
    )
    Page<Center> search(@Param("search") String search, Pageable pageable);

//    Page<Center> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
