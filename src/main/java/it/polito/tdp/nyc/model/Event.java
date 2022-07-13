package it.polito.tdp.nyc.model;

public class Event implements Comparable<Event>{

	public enum EventType{
		
		INIZIO_HS,
		FINE_HS,
		NUOVO_QUARTIERE;
	}
	
	private int time;
	private EventType type;
	private int tecnico; //numero corrispondente al tecnico
	
	
	
	public Event(int time, EventType type, int tecnico) {
		
		this.time = time;
		this.type = type;
		this.tecnico = tecnico;
	}



	public int getTime() {
		return time;
	}



	public EventType getType() {
		return type;
	}



	public int getTecnico() {
		return tecnico;
	}



	@Override
	public int compareTo(Event o) {
		// TODO Auto-generated method stub
		return this.time-o.time;
	}
	
}
