package com.idrene.emefana.repositories;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.idrene.emefana.AbstractIntegrationTest;
import com.idrene.emefana.domain.User;

public class PersonaRepositoryTest extends AbstractIntegrationTest{
	
	@Value("${mail.smtp.host}") 
	private String mailHost;
	
	 @Autowired 
	 private PersonRepository repository;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMongoOps() {
		System.out.println(mailHost);
		 repository.deleteAll();
		 assertTrue(0 == repository.count());
	     User person = new User("IDRENE","iddy@gmail.com","user_test");
	     person.setFirstName("Iddy");
	     person.setLastName("Magohe");
	     
	     person = repository.save(person);
	     
	     User lastNameResults = repository.findByLastName("Magohe").get(0);
	     User firstNameResults = repository.findByFirstNameLike("Id*").get(0);
	     assertNotNull(lastNameResults);
	     assertNotNull(firstNameResults);
	}

}
