package com.vmo.management_fresher.service;

import com.vmo.management_fresher.model.Role;

import java.util.List;

public interface RoleService {
    Role createRole(String uid, Role request);
    Role updateRole(String uid, String name, Role request);
    String deleteRole(String name);
    Role getById(String name);
    List<Role> getAll();
}
