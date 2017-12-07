/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
 *
 * This file is part of DiscourseDB.
 *
 * DiscourseDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * DiscourseDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DiscourseDB.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street, 
 * Fifth Floor, Boston, MA 02110-1301  USA
 *******************************************************************************/
package edu.cmu.cs.lti.discoursedb.system.model.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.model.TimedBE;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * A DiscourseDB user. 
 * Used for authentication and tracking of annotation authorship.
 * Might be complemented with Domain ACLs in the future for domain object-level permissions.
 * 
 * @author Oliver Ferschke
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "system_user")
public class SystemUser extends TimedBE implements UserDetails {

	@Id
	@Column(name = "id_system_user", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE)
	private Long id;

	private String realname;

	private String username;

	private String email;
	
	@Column(name = "password_hash")
	private String passwordHash;

	@Override
	public String toString() {
		return username;
	}
	
	
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "system_user_roles", joinColumns = @JoinColumn(name = "id_system_user"))
    @Column(name = "role", nullable = false, length = 171)
    @Enumerated(EnumType.STRING)
	private Set<SystemUserRole> roles = new HashSet<SystemUserRole>();

	@OneToMany(fetch = FetchType.EAGER, mappedBy="systemUser")
	private Set<SystemUserProperty> properties = new HashSet<SystemUserProperty>();;


    @OneToMany(fetch = FetchType.EAGER, mappedBy="systemUser")
    private Set<SystemUserRight> rights = new HashSet<SystemUserRight>();;

    @Transient
    private List<GrantedAuthority> authorities =  new ArrayList<GrantedAuthority>();;
    
    @Transient
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (true || authorities == null) {
			authorities = new ArrayList<GrantedAuthority>();
			for (SystemUserRole r : this.getRoles()) {
				authorities.add(new SimpleGrantedAuthority("ROLE:" + r.name()));
			}
			for (SystemUserRight r : this.getRights()) {
				authorities.add(new SimpleGrantedAuthority(r.getDatabaseName()));
			}
		}
		return authorities;
	}	

	@Override
	public String getPassword() {
		return passwordHash;
	}

	@Transient
    @Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Transient
    @Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Transient
    @Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Transient
    @Override
	public boolean isEnabled() {
		return true;
	}


}
