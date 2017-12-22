package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import model.Canale;
import model.Gruppo;
import model.Utente;

public class CanaleProxy extends Canale {
	
	DataSource dataSource;
	
	CanaleProxy(DataSource ds){
		
		dataSource = ds;
	}
	
	public Set<Utente> getMembri() { 
		Set<Utente> utenti = new HashSet<>();
		Connection connection = this.dataSource.getConnection();
		try {
			PreparedStatement statement;
			String query = "select * from utente where id IN select id_utente from iscritto_canale where nome_canale = ?";
			statement = connection.prepareStatement(query);
			statement.setString(1, this.getNome());
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				Utente utente = new Utente();
				utente.setId(result.getLong("id"));				
				utente.setNome(result.getString("nome"));
				utente.setCognome(result.getString("cognome"));
				long secs = result.getDate("data_nascita").getTime();
				utente.setDataDiNascita(new java.util.Date(secs));
				utenti.add(utente);
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
		this.setMembri(utenti);
		return super.getMembri(); 
	}
	
	
	public Set<Gruppo> getGruppi() { 
		Set<Gruppo> gruppi = new HashSet<>();
		Connection connection = this.dataSource.getConnection();
		try {
			PreparedStatement statement;
			String query = "select * from gruppo where nome IN select nome_gruppo from gruppo_canale where nome_canale = ?";
			statement = connection.prepareStatement(query);
			statement.setString(1, this.getNome());
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				Gruppo gruppo = new GruppoProxy(dataSource);			
				gruppo.setNome(result.getString("nome"));
				gruppi.add(gruppo);
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
		this.setGruppi(gruppi);
		return super.getGruppi(); 
	}

}
