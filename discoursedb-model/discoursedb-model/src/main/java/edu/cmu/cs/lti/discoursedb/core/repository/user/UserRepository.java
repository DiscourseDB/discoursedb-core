package edu.cmu.cs.lti.discoursedb.core.repository.user;

import java.util.List;
import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface UserRepository extends CoreBaseRepository<User,Long>{
    
	public Optional<User> findById(Long id);    	
	public long countByRealname(String realname);
    public List<User> findAllByUsername(String username);

    /*
     * The following methods need to be implemented based on multi-valued DataSources
     */
    
//	public Optional<User> findByDataSource(DataSourceInstance source);
//	public Optional<User> findByEntitySourceIdAndUsername(String entitySourceId, String username);
//	public List<User> findAllBySourceType(DataSourceTypes type);
//	public List<User> findAllByDataset(String dataSetName);


    /*
     * The following methods need to be replaced by the above
     */
    
	public Optional<User> findBySourceIdAndUsername(String sourceId, String username);
    public List<User> findAllBySourceId(String sourceId);
}
