package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Canale;
import model.Gruppo;
import model.Utente;
import persistence.dao.CanaleDao;
import persistence.dao.GruppoDao;
import persistence.dao.UtenteDao;

public class CanaleDaoJDBC implements CanaleDao {
	
	DataSource dataSource;
	
	CanaleDaoJDBC(DataSource d){
		
		dataSource = d;
	}

	@Override
	public void save(Canale canale) {

		Connection connection = dataSource.getConnection();
		
		try{
			String insert = "insert into canale(nome, descrizione) values (?,?)";
			PreparedStatement statement = connection.prepareStatement(insert);
			statement.setString(1, canale.getNome());
			statement.setString(2, canale.getDescrizione());
			
			statement.executeUpdate();
			// salviamo anche tutti gli utenti del canale ed i gruppi in CASACATA
			this.updateMembri(canale, connection);
			this.updateGruppi(canale, connection);
			
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
		}
	}
	
	private void updateMembri(Canale canale, Connection connection) throws SQLException {
		
		UtenteDao utenteDao = new UtenteDaoJDBC(dataSource);
		for (Utente utente : canale.getMembri()) {
			if (utenteDao.findByPrimaryKey(utente.getId()) == null){
				utenteDao.save(utente);
			}
			
			String iscrittoCanale = "select id from iscritto_canale where id_utente=? AND nome_canale=?";
			PreparedStatement statementIscritto = connection.prepareStatement(iscrittoCanale);
			statementIscritto.setLong(1, utente.getId());
			statementIscritto.setString(2, canale.getNome());
			ResultSet result = statementIscritto.executeQuery();
			if(result.next()){
				String update = "update iscritto_canale SET nome_canale = ? WHERE id = ?"; //da controllare
				PreparedStatement statement = connection.prepareStatement(update);
				statement.setString(1, canale.getNome());
				statement.setLong(2, result.getLong("id"));
				statement.executeUpdate();
			}else{			
				String iscrivi = "insert into iscritto_canale (id, id_utente, nome_canale) values (?,?,?)";
				PreparedStatement statementIscrivi = connection.prepareStatement(iscrivi);
				Long id = IdBroker.getId(connection);
				statementIscrivi.setLong(1, id);
				statementIscrivi.setLong(2, utente.getId());
				statementIscrivi.setString(3, canale.getNome());
				statementIscrivi.executeUpdate();
			}
		}
	}
	
	private void removeForeignKeyFromUtente(Canale canale, Connection connection) throws SQLException {
		for (Utente utente : canale.getMembri()) {
			String update = "update iscritto_canale SET nome_canale = NULL WHERE id_utente = ?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setLong(1, utente.getId());
			statement.executeUpdate();
		}	
	}
	
	private void updateGruppi(Canale canale, Connection connection) throws SQLException {
		
		GruppoDao gruppoDao = new GruppoDaoJDBC(dataSource);
		for (Gruppo gruppo : canale.getGruppi()) {
			if (gruppoDao.findByPrimaryKey(gruppo.getNome()) == null){
				gruppoDao.save(gruppo);
			}
			
			String gruppoCanale = "select id from gruppo_canale where nome_gruppo=? AND nome_canale=?";
			PreparedStatement statementGruppoCanale = connection.prepareStatement(gruppoCanale);
			statementGruppoCanale.setString(1, gruppo.getNome());
			statementGruppoCanale.setString(2, canale.getNome());
			ResultSet result = statementGruppoCanale.executeQuery();
			if(result.next()){
				String update = "update gruppo_canale SET nome_canale = ? WHERE id = ?"; //da controllare
				PreparedStatement statement = connection.prepareStatement(update);
				statement.setString(1, canale.getNome());
				statement.setLong(2, result.getLong("id"));
				statement.executeUpdate();
			}else{			
				String iscrivi = "insert into gruppo_canale (id, nome_gruppo, nome_canale) values (?,?,?)";
				PreparedStatement statementIscrivi = connection.prepareStatement(iscrivi);
				Long id = IdBroker.getId(connection);
				statementIscrivi.setLong(1, id);
				statementIscrivi.setString(2, gruppo.getNome());
				statementIscrivi.setString(3, canale.getNome());
				statementIscrivi.executeUpdate();
			}
		}
	}
	
	private void deleteGruppi(Canale canale, Connection connection) throws SQLException {
		GruppoDao gruppoDao = new GruppoDaoJDBC(dataSource);
		for (Gruppo gruppo : canale.getGruppi()) {
			gruppoDao.delete(gruppo);
		}	
	}


	
	//implementato con lazy load (proxy)
	@Override
	public Canale findByPrimaryKey(String nome) {
		Connection connection = this.dataSource.getConnection();
		Canale canale = null;
		try {
			PreparedStatement statement;
			String query = "select * from canale where nome = ?";
			statement = connection.prepareStatement(query);
			statement.setString(1, nome);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				canale = new CanaleProxy(dataSource);
				canale.setNome(result.getString("nome"));				
				canale.setDescrizione(result.getString("descrizione"));
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
		return canale;
	}

	@Override
	public List<Canale> findAll() {
		
		Connection connection = this.dataSource.getConnection();
		List<Canale> canali = new ArrayList<>();
		try {			
			Canale canale;
			PreparedStatement statement;
			String query = "select * from canale";
			statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				canale = findByPrimaryKey(result.getString("nome"));
				canali.add(canale);
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
		return canali;
	}

	@Override
	public void update(Canale canale) {
		Connection connection = this.dataSource.getConnection();
		try {
			String update = "update canale SET descrizione = ? WHERE nome = ?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setString(1, canale.getDescrizione());
			statement.setString(2, canale.getNome());
			
			statement.executeUpdate();
			this.updateMembri(canale, connection); 
			this.updateGruppi(canale, connection);
			//connection.commit();
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
		}
		
	}

	@Override
	public void delete(Canale canale) {

		Connection connection = this.dataSource.getConnection();
		try {
			String delete = "delete FROM canale WHERE nome = ? ";
			PreparedStatement statement = connection.prepareStatement(delete);
			statement.setString(1, canale.getNome());

			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);			
			this.removeForeignKeyFromUtente(canale, connection);     			
			this.deleteGruppi(canale, connection);
			
			statement.executeUpdate();
			connection.commit();
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

}
