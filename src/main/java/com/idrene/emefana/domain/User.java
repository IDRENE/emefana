/**
 * 
 */
package com.idrene.emefana.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.idrene.emefana.security.EMEFANA_ROLES;

/**
 * @author iddymagohe
 *
 */
@Document(collection = "users")
@TypeAlias("users")
public class User {
    
	@Id
	@Setter @Getter private String id;
	
	@Setter @Getter private String firstName;
	
	@Setter @Getter private String lastName;
	
	@Indexed
	@Setter @Getter private  String emailAddress;
	
	@Indexed
	@Getter @Setter private String associatedProvider;
	
	@Getter @Setter private String listingRole;
	
	@JsonIgnore
	@Setter @Getter private String password;
	
	@JsonIgnore
	@Getter @Setter Set<EMEFANA_ROLES> roles = new HashSet<>();

	
	
	/**
	 * @param id
	 * @param emailAddress
	 * @param password
	 */
	public User(String id, String emailAddress, String password) {
		this.id = id;
		this.emailAddress = emailAddress;
		this.password = password;
	}
	
	public User(){
		
	}


	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName="
				+ lastName + "]";
	}
	
	@JsonIgnore
	public Optional<String> getOAssociatedProvider(){
		return Optional.ofNullable(StringUtils.trimToNull(associatedProvider));
	}
	
	@JsonIgnore
	public Optional<String> getOemailAddress(){
		return Optional.ofNullable(emailAddress);
	}
	
	@JsonIgnore
	public Optional<String> getOuserId(){
		return Optional.ofNullable(id);
	}
	
	/**
	 * @param userRoles
	 */
	public void addRoles(EMEFANA_ROLES ... userRoles){
		Assert.notNull(userRoles, "Role must not be null");
		Arrays.stream(userRoles).forEach(roles::add);
	}
}
