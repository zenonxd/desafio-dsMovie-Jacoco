package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository repository;

	@Mock
	private CustomUserUtil customUserUtil;

	private UserEntity user;
	private String username, invalidUsername;
	List<UserDetailsProjection> result;

	@BeforeEach
	void setUp() throws Exception {
		user = UserFactory.createUserEntity();
		username = customUserUtil.getLoggedUsername();
		invalidUsername = "";
		result = UserDetailsFactory.createCustomAdminUser(username);

		Mockito.when(customUserUtil.getLoggedUsername()).thenReturn(username);
		Mockito.when(repository.findByUsername(username)).thenReturn(Optional.of(user));


	}

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {

		user = service.authenticated();

		Assertions.assertNotNull(user);
		Assertions.assertEquals("maria@gmail.com", user.getUsername());
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		Mockito.when(repository.findByUsername(username)).thenReturn(empty());

		Assertions.assertThrows(UsernameNotFoundException.class, () -> service.authenticated());
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
		Mockito.when(repository.searchUserAndRolesByUsername(username)).thenReturn(result);

		UserDetails userDetails = service.loadUserByUsername(username);

		Assertions.assertNotNull(userDetails);
		Assertions.assertEquals(username, userDetails.getUsername());
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		result.clear();
		Mockito.when(repository.searchUserAndRolesByUsername(invalidUsername)).thenReturn(result);
		Assertions.assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(invalidUsername));
	}
}
