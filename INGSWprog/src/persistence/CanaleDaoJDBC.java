package persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import model.Canale;
import persistence.dao.CanaleDao;

public class CanaleDaoJDBC implements CanaleDao {
	
	DataSource dataSource;
	
	CanaleDaoJDBC(DataSource d){
		
		dataSource = d;
	}

	@Override
	public void save(Canale canale) {

		Connection connection = dataSource.getConnection();
		
		/*try{
			String insert = "insert into canale(nome, descrizione) values (?,?)";
			
		
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException excep) {
					throw new PersistenceException(e.getMessage());
				}
			} 
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}*/
	}

	@Override
	public Canale findByPrimaryKey(String nome) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Canale> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Canale canale) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dekete(Canale canale) {
		// TODO Auto-generated method stub
		
	}

}
