package com.bbytes.ccenter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.ccenter.domain.User;

/**
 * Spring Data MongoDB repository for the User entity.
 */
public interface UserRepository extends MongoRepository<User, String> {

	
	User findByClientId(String clientId);
}
