package com.vmo.management_fresher.repository;

import com.vmo.management_fresher.model.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, String> {
    Optional<Account> findByUsername(String username);
    Boolean existsByUsername(String username);

    Boolean existsByUsernameAndIdIsNot(String username, String id);

    @Modifying
    @Transactional
    @Query(value = "update Account a" +
            " set a.password = :password" +
            " where a.id = :accountId")
    void updatePassword(@Param("accountId") String accountId, @Param("password") String password);

}
