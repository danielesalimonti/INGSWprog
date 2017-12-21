package persistence.dao;

import java.util.List;
import model.Commento;

public interface CommentoDao {
	
	public void save(Commento commento);  // Create
	public Commento findByPrimaryKey(Long id);     // Retrieve
	public List<Commento> findAll();       
	public void update(Commento commento); //Update
	public void delete(Commento commento);

}
