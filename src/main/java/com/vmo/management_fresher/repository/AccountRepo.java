package com.vmo.management_fresher.repository;

import com.vmo.management_fresher.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepo extends JpaRepository<Account, String> {
    Boolean existsByUsername(String username);

    Boolean existsByUsernameAndIdIsNot(String username, String id);

}
