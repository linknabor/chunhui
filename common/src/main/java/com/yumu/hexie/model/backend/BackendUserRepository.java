package com.yumu.hexie.model.backend;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BackendUserRepository extends JpaRepository<BackendUser, Long> {
	public BackendUser findByUsername(String username);
}
