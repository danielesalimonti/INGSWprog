package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Gruppo;
import model.Utente;
import persistence.dao.GruppoDao;
import persistence.dao.UtenteDao;

public class GruppoDaoJDBC implements GruppoDao{

	private DataSource dataSource;

	public GruppoDaoJDBC(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	
	@Override
	public void save(Gruppo gruppo) {
		Connection connection = dataSource.getConnection();
		
		try{
			String insert = "insert into gruppo(nome) values (?)";
			PreparedStatement statement = connection.prepareStatement(insert);
			statement.setString(1, gruppo.getNome());
			
			statement.executeUpdate();
			// salviamo anche tutti gli utenti del canale in CASACATA
			this.updateMembri(gruppo, connection);
		
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
	
	private void updateMembri(Gruppo gruppo, Connection connection) throws SQLException {
		
		UtenteDao utenteDao = new UtenteDaoJDBC(dataSource);
		for (Utente utente : gruppo.getMembri()) {
			if (utenteDao.findByPrimaryKey(utente.getId()) == null){
				utenteDao.save(utente);
			}
			
			String iscrittoCanale = "select id from iscritto_gruppo where id_utente=? AND nome_gruppo=?";
			PreparedStatement statementIscritto = connection.prepareStatement(iscrittoCanale);
			statementIscritto.setLong(1, utente.getId());
			statementIscritto.setString(2, gruppo.getNome());
			ResultSet result = statementIscritto.executeQuery();
			if(result.next()){
				String update = "update iscritto_gruppo SET nome_gruppo = ? WHERE id = ?"; //da controllare
				PreparedStatement statement = connection.prepareStatement(update);
				statement.setString(1, gruppo.getNome());
				statement.setLong(2, result.getLong("id"));
				statement.executeUpdate();
			}else{			
				String iscrivi = "insert into iscritto_gruppo (id, id_utente, nome_gruppo) values (?,?,?)";
				PreparedStatement statementIscrivi = connection.prepareStatement(iscrivi);
				Long id = IdBroker.getId(connection);
				statementIscrivi.setLong(1, id);
				statementIscrivi.setLong(2, utente.getId());
				statementIscrivi.setString(3, gruppo.getNome());
				statementIscrivi.executeUpdate();
			}
		}
	}

	private void removeForeignKeyFromUtente(Gruppo gruppo, Connection connection) throws SQLException {
		for (Utente utente : gruppo.getMembri()) {
			String update = "update iscritto_gruppo SET nome_gruppo = NULL WHERE id_utente = ?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setLong(1, utente.getId());
			statement.executeUpdate();
		}	
	}
	
	//implementato con lazy load
	@Override
	public Gruppo findByPrimaryKey(String nome) {
		Connection connection = this.dataSource.getConnection();
		Gruppo gruppo = null;
		try {
			PreparedStatement statement;
			String query = "select * from gruppo where nome = ?";
			statement = connection.prepareStatement(query);
			statement.setString(1, nome);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				gruppo = new GruppoProxy(dataSource);
				gruppo.setNome(result.getString("nome"));				
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
		return gruppo;
	}

	@Override
	public List<Gruppo> findAll() {
		
		Connection connection = this.dataSource.getConnection();
		List<Gruppo> gruppi = new ArrayList<>();
		try {			
			Gruppo gruppo;
			PreparedStatement statement;
			String query = "select * from gruppo";
			statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				gruppo = findByPrimaryKey(result.getString("nome"));
				gruppi.add(gruppo);
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
		return gruppi;
	}

	@Override
	public void update(Gruppo gruppo) {
		Connection connection = this.dataSource.getConnection();
		try {
			
			this.updateMembri(gruppo, connection); 
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
	public void delete(Gruppo gruppo) {
		Connection connection = this.dataSource.getConnection();
		try {
			String delete = "delete FROM gruppo WHERE nome = ? ";
			PreparedStatement statement = connection.prepareStatement(delete);
			statement.setString(1, gruppo.getNome());

			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);			
			this.removeForeignKeyFromUtente(gruppo, connection);     			
			
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
