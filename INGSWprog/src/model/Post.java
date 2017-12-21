package model;

import java.util.Set;

public class Post {
	
	private Utente creatore;
	private String contenuto;
	private Long id;
	private Set<Commento> commenti;
	
	Post(){}
	
	Post(Utente cr, String co){
		
		creatore = cr;
		contenuto = co;
	}

	public Utente getCreatore() {
		return creatore;
	}

	public void setCreatore(Utente creatore) {
		this.creatore = creatore;
	}

	public String getContenuto() {
		return contenuto;
	}

	public void setContenuto(String contenuto) {
		this.contenuto = contenuto;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Commento> getCommenti() {
		return commenti;
	}

	public void setCommenti(Set<Commento> commenti) {
		this.commenti = commenti;
	}
	
	public void addCommento (Commento c){
		
		this.getCommenti().add(c);
	}
	
	public void removeCommento (Commento c){
		
		this.getCommenti().remove(c);
	}
	

}
