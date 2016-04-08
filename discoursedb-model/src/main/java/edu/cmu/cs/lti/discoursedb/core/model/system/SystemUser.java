package edu.cmu.cs.lti.discoursedb.core.model.system;

import java.util.Set;

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
import javax.persistence.Table;

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
public class SystemUser extends TimedBE {

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "system_user_roles", joinColumns = @JoinColumn(name = "id_system_user"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
	private Set<SystemUserRole> roles;

}
