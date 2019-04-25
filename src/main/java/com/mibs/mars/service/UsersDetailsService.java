package com.mibs.mars.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mibs.mars.entity.Users;
import com.mibs.mars.repository.UsersRepository;

@Service
public class UsersDetailsService implements UserDetailsService{

	@Autowired
	private UsersRepository repository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//Users user = repository.findByLogin( username );
		Users user = repository.findByEmail( username.toLowerCase() );
		if (user == null) {
			throw new UsernameNotFoundException("User not found for username: [" + username + "]" );
		}
		return new UsersDetails(user);
	}

}
