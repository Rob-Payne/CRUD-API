package com.example.CRUD;

import com.example.CRUD.dao.UserRepository;
import com.example.CRUD.model.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.print.attribute.standard.Media;
import javax.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
class CrudApplicationTests {

	@Autowired
	MockMvc mvc;

	@Autowired
	UserRepository UserRepository;

	@Test
	void contextLoads() {
	}



	@Test
	@Transactional
	@Rollback
	public void getUserListTest() throws Exception {

		User myUser = new User();
		myUser.setEmail("Aanand@softwarefactory.com");
		myUser.setPassword("Notapassword");
		UserRepository.save(myUser);

		MockHttpServletRequestBuilder getUsersRequest = get("/users")
				.contentType(MediaType.APPLICATION_JSON);

		this.mvc.perform(getUsersRequest)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].email", is("Aanand@softwarefactory.com")));
	}

	@Test
	@Transactional
	@Rollback
	public void getUserByIdTest() throws Exception {

		User myUser = new User();
		myUser.setPassword("123");
		myUser.setEmail("TestUser@Hi.com");
		User newUser = UserRepository.save(myUser);
		Long newUserId = newUser.getId();

		MockHttpServletRequestBuilder getUserById = get("/users/"+newUserId)
				.contentType(MediaType.APPLICATION_JSON);

		this.mvc.perform(getUserById)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email", is("TestUser@Hi.com")));
	}

	@Test
	@Transactional
	@Rollback
	public void insertNewUserTest() throws Exception{
		//Create user
		MockHttpServletRequestBuilder insertNewUser = post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\":\"email@domain.com\",\"password\":\"123\"}");

		//Make sure user was created
		this.mvc.perform(insertNewUser)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email",is("email@domain.com")));
	}

	@Test
	@Transactional
	@Rollback
	public void patchExistingUser() throws Exception{
		//Create user
		User myUser = new User();
		myUser.setPassword("123");
		myUser.setEmail("name@domain.com");
		Long myUserId = UserRepository.save(myUser).getId();

		//create patch request
		MockHttpServletRequestBuilder patchUser = patch("/users/"+myUserId)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\":\"domain@name.com\"}");

		this.mvc.perform(patchUser)
				.andExpect(jsonPath("$.email", is("domain@name.com")));
	}


	@Test
	@Transactional
	@Rollback
	public void deleteUserByIdTest() throws Exception {
		//Create new user
		User myUser = new User();
		myUser.setPassword("123");
		myUser.setEmail("TestUser@Hi.com");
		User newUser = UserRepository.save(myUser);
		Long newUserId = newUser.getId();

		//Get request to check for new user
		MockHttpServletRequestBuilder verifyUser = get("/users/" + newUserId)
				.contentType(MediaType.APPLICATION_JSON);

		this.mvc.perform(verifyUser)
				.andExpect(jsonPath("$.email", is("TestUser@Hi.com")))
				.andExpect(status().isOk());

		//Delete user
		MockHttpServletRequestBuilder deleteUser = delete("/users/" + newUserId)
				.contentType(MediaType.APPLICATION_JSON);

		this.mvc.perform(deleteUser)
				.andExpect(status().isOk());

		//Check to see that user was deleted
		MockHttpServletRequestBuilder checkDeletedUser = get("/users/" + newUserId)
				.contentType(MediaType.APPLICATION_JSON);

		this.mvc.perform(checkDeletedUser)
				.andExpect(jsonPath("$").doesNotExist())
				.andExpect(status().isOk());
	}

	@Test
	@Transactional
	@Rollback
	public void authenticateUserTest() throws Exception {
		//Create several users
		User my1stUser = new User();
		my1stUser.setPassword("123");
		my1stUser.setEmail("TestUser1@Hi.com");
		User newUser1 = UserRepository.save(my1stUser);

		User my2ndUser = new User();
		my2ndUser.setPassword("321");
		my2ndUser.setEmail("TestUser2@Hi.com");
		User newUser2 = UserRepository.save(my2ndUser);

		User my3rdUser = new User();
		my3rdUser.setPassword("222");
		my3rdUser.setEmail("TestUser3@Hi.com");
		User newUser = UserRepository.save(my3rdUser);

		//Establish payload
		String credentials = "{\"email\":\"TestUser1@Hi.com\",\"password\":\"123\"}";

		//Pass payload
		MockHttpServletRequestBuilder requestUser = post("/users/authenticate")
				.contentType(MediaType.APPLICATION_JSON)
				.content(credentials);
		//evaluate result
		this.mvc.perform(requestUser)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.authenticated",is("true")));

		//Establish bad credentials payload
		String badcredentials = "{\"email\":\"TestUser1@Hi.com\",\"password\":\"321\"}";

		//Pass bad payload
		MockHttpServletRequestBuilder requestbadUser = post("/users/authenticate")
				.contentType(MediaType.APPLICATION_JSON)
				.content(badcredentials);

		//evaluate result
		this.mvc.perform(requestbadUser)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.authenticated",is("false")));

	}



}
