package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

public interface DiscoursePartRepository extends BaseRepository<DiscoursePart,Long>{
    
	Optional<DiscoursePart> findOneByName(@Param("name") String name);
	
	List<DiscoursePart> findAllByName(@Param("name") String name);
	List<DiscoursePart> findAllByType(@Param("discoursePartType") String type);
	
	@Query("select dp from DiscoursePart dp left join fetch dp.annotations aa "
			+ "left join fetch aa.annotations ai left join fetch ai.features feat "
			+ " where dp.type=:discoursePartType")
	List<DiscoursePart> findExtendedByType(@Param("discoursePartType") String discoursePartType);
	
	
	@Query("select dp from DiscoursePart dp where "
			+ "((select count(ai) from dp.annotations aa join aa.annotations ai where ai.type like 'Degenerate') = 0)"
			+ " and type=:discoursePartType")
	Page<DiscoursePart> findAllNonDegenerateByType(@Param("discoursePartType") String discoursePartType, Pageable pageable);

	@Query("select dp from DiscourseToDiscoursePart dpd left join dpd.discourse d "
			+ " left join dpd.discoursePart dp "
			+ " where d.id=:discourseId "
			+ " and dp.type=:discoursePartType")
	Page<DiscoursePart> findAllByDiscourseAndType(@Param("discoursePartType") String discoursePartType, @Param("discourseId") Long discourseId, Pageable pageable);

	/*@Query(value = "select dp.* from discourse_has_discourse_part dpd left join discourse d on d.id_discourse=dpd.fk_discourse "
			+ " left join discourse_part dp on dp.id_discourse_part = dpd.fk_discourse_part "
			+ " where d.id=:discourseId "
			+ " and dp.type=:discoursePartType", nativeQuery=true)
	Page<DiscoursePart> findAllByDiscourseAndTypeNative(@Param("discoursePartType") String discoursePartType, @Param("discourseId") Long discourseId, Pageable pageable);*/
	
	@Query("select dp from DiscoursePart dp left join dp.dataSourceAggregate dsa left join dsa.sources dsi where dsi.entitySourceId=:id")
	Optional<DiscoursePart> findOneByDataSourceId(@Param("id") String id);
		
	
	@Query(value = "select * from discourse_part dp where fk_annotation not in " +
				      "(select fk_annotation from annotation_instance where type=:annotationType)",
		nativeQuery=true)
	List<DiscoursePart> findAllNotAnnotatedWithType(@Param("annotationType") String type);
	
	@Query("select dp from DiscoursePart dp where "
			+ "((select count(ai) from dp.annotations aa join aa.annotations ai where ai.type like :annotationType) = 0)")
	Page<DiscoursePart> findAllNotAnnotatedWithTypePaged(@Param("annotationType") String type, Pageable pageable);

	@Query("select type, count(*) as count from DiscoursePart dp group by type")
	List<Object[]> countsByType();	

 	@Query("select dp.type, count(*) from DiscourseToDiscoursePart dpd left join dpd.discourse d "
			+ " left join dpd.discoursePart dp "
			+ " where d=:discourse group by dp.type")
	List<Object[]> countsByTypeAndDiscourse(@Param("discourse") Discourse discourse); 

	@Query(value= "select dp.type, count(*) from discourse_has_discourse_part dpd left join discourse d on dpd.fk_discourse = d.id_discourse "
			+ " left join discourse_part dp on dpd.fk_discourse_part = dp.id_discourse_part "
			+ " where d.id_discourse=:discourseId group by dp.type", nativeQuery=true)
	List<Object[]> countsByTypeAndDiscourseNative(@Param("discourseId") Long discourseId);

	/*@Query("select dp1 from User u "
			+ "left join Content conte on conte.user=u "
			+ "left join Contribution contr on contr.firstRevision = conte "
			+ "left join contr.contributionPartOfDiscourseParts cpdp  "
			+ "left join cpdp.discoursePart dp1 "
			+ "where u=:u")*/
	@Query("select dp1 from DiscoursePartContribution cpdp "
			+ "inner join cpdp.contribution contr  "
			+ "inner join contr.firstRevision conte "
			+ "left join cpdp.discoursePart dp1 "
			+ "where conte.author=?1")
	Set<DiscoursePart> findAllThatIncludesUser(User u);
	

	@Query("select dp1 from DiscoursePartContribution cpdp "
			+ "inner join cpdp.contribution contr  "
			+ "inner join contr.firstRevision conte "
			+ "left join cpdp.discoursePart dp1 "
			+ "where conte.author=?1")
	Page<DiscoursePart> findAllThatIncludesUserPaged(User u, Pageable p);




}
