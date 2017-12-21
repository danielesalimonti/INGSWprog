package model;

public class Commento {
	
	private Long id;
	private String contenuto;
	private Utente creatore;
	
	Commento(){}
	
	Commento(String co, Utente cr){
		
		contenuto = co;
		creatore = cr;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getContenuto() {
		return contenuto;
	}
	
	public void setContenuto(String contenuto) {
		this.contenuto = contenuto;
	}
	
	public Utente getCreatore() {
		return creatore;
	}
	
	public void setCreatore(Utente creatore) {
		this.creatore = creatore;
	}
	
	

}
