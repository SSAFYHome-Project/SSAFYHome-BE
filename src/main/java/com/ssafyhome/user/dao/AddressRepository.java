package com.ssafyhome.user.dao;

import com.ssafyhome.user.dto.Address;
import com.ssafyhome.user.dto.TitleType;
import com.ssafyhome.user.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
	List<Address> findByUser(User user);
	Optional<Address> findByUserAndTitle(User user, TitleType title);
}