package persistence.dao;

import java.util.List;

import model.Canale;

public interface CanaleDao {
	
	public void save(Canale canale);
	public Canale findByPrimaryKey(String nome);
	public List<Canale> findAll();
	public void update(Canale canale);
	public void delete(Canale canale);
	

}
