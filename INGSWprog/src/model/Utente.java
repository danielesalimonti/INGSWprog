package model;

import java.util.Date;

public class Utente {
	
	private String nome;
	private String cognome;
	private Date dataDiNascita;
	private Long id;
	
	public Utente(){}
	
	public Utente(String n, String c, Date ddn){
		
		nome = n;
		cognome = c;
		dataDiNascita = ddn;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public Date getDataDiNascita() {
		return dataDiNascita;
	}

	public void setDataDiNascita(Date dataDiNascita) {
		this.dataDiNascita = dataDiNascita;
	}
	
	
	
	

}
