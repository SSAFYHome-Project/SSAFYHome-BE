package com.ssafyhome.user.dao;

import com.ssafyhome.user.dto.Address;
import com.ssafyhome.user.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
	List<Address> findByUser(User user);
}