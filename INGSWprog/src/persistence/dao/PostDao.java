package persistence.dao;

import java.util.List;
import model.Post;

public interface PostDao {
	
	public void save(Post post);
	public Post findByPrimaryKey(Long id);
	public List<Post> findAll();       
	public void update(Post post); 
	public void delete(Post post);

}
