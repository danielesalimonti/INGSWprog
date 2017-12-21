package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import model.Utente;
import persistence.dao.UtenteDao;

public class UtenteDaoJDBC implements UtenteDao{
	
	private DataSource dataSource;
	
	UtenteDaoJDBC(DataSource ds){
		
		dataSource = ds;
	}
	
	@Override
	public void save(Utente utente) {
		Connection connection = dataSource.getConnection();
		try {
			Long id = IdBroker.getId(connection);
			utente.setId(id);
			String insert = "insert into utente(nome, cognome, data_nascita) values (?,?,?)";
			PreparedStatement statement = connection.prepareStatement(insert);
			statement.setString(1, utente.getNome());
			statement.setString(2, utente.getCognome());
			long secs = utente.getDataDiNascita().getTime();
			statement.setDate(3, new java.sql.Date(secs));
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		
	}

	@Override
	public Utente findByPrimaryKey(Long id) {
		Connection connection = this.dataSource.getConnection();
		Utente utente = null;
		try {
			PreparedStatement statement;
			String query = "select * from utente where id = ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, id);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				utente = new Utente();
				utente.setId(result.getLong("id"));				
				utente.setNome(result.getString("nome"));
				utente.setCognome(result.getString("cognome"));
				long secs = result.getDate("data_nascita").getTime();
				utente.setDataDiNascita(new java.util.Date(secs));
			}
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}	
		return utente;
	}

	@Override
	public List<Utente> findAll() {
		Connection connection = this.dataSource.getConnection();
		List<Utente> utenti = new LinkedList<>();
		try {
			Utente utente;
			PreparedStatement statement;
			String query = "select * from utente";
			statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				utente = new Utente();
				utente.setId(result.getLong("id"));				
				utente.setNome(result.getString("nome"));
				utente.setCognome(result.getString("cognome"));
				long secs = result.getDate("data_nascita").getTime();
				utente.setDataDiNascita(new java.util.Date(secs));
				
				utenti.add(utente);
			}
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		}	 finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return utenti;
	}

	@Override
	public void update(Utente utente) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Utente utente) {
		// TODO Auto-generated method stub
		
	}

}
