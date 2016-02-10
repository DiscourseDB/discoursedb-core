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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.rest.core.annotation.Description;

import edu.cmu.cs.lti.discoursedb.core.model.BaseTypeEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true, exclude={"contexts"})
@ToString(callSuper=true, exclude={"contexts"})
@Entity
@Table(name="context_type")
@Description("Defines the type of an a context entity.")
public class ContextType extends BaseTypeEntity implements Serializable {

	private static final long serialVersionUID = 9191265196419948023L;

	@Id
	@Column(name="id_content_type", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	@Description("The primary key.")
	private Long id;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="type")
	@Description("A set of all context entities with this type.")
	private Set<Context> contexts = new HashSet<Context>();
	
}
