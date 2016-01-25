package edu.cmu.cs.lti.discoursedb.core.model.macro;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.UntimedBaseEntity;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>A Discourse represents the broad context of interactions that might come from
 * multiple datasets. For example, a Discourse could represent an installment of
 * an online course. All interactions in the context of this course -
 * independent from the source dataset - will be associated with this Discourse
 * instance. Another installment of the same course would be represented by a
 * new Discourse instance.</p> 
 * 
 * <p>A Discourse is associated to one or more
 * DiscoursePart instances which represent sub-spaces in the realm of the
 * Discourse. That is, an online course with a discussion forum and chat would
 * have two DiscoursePart instances associated with its Discourse instance which
 * represent these two discussion spaces.</p>
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true, exclude={"discourseToDiscourseParts","users"})
@ToString(callSuper=true, exclude={"discourseToDiscourseParts","users"})
@Entity
@Table(name = "discourse", indexes = { @Index(name = "discourseNameIndex", columnList = "name") })
public class Discourse extends UntimedBaseEntity implements Serializable {

	private static final long serialVersionUID = -3736157436274230022L;

	public Discourse(String name){
		Assert.hasText(name);
		this.name=name;
	}

	@Id
	@Column(name = "id_discourse", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;

	@Column(updatable=false, unique=true)
	private String name;

	@OneToMany(mappedBy = "discourse")
	private Set<DiscourseToDiscoursePart> discourseToDiscourseParts = new HashSet<DiscourseToDiscoursePart>();

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "discourses")
	private Set<User> users;
	
}
