package it.polito.tdp.nyc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.nyc.model.Event.EventType;

public class Simulatore {

	//DATI INGRESSO
	private Graph <City,DefaultWeightedEdge> grafo;
	private List<City> cities;
	private City partenza;
	private int N;
	
	//DATI USCITA
	private int durata;
	private List<Integer> revisionati;
	
	//MODELLO DEL MONDO
	private List<City> daVisitare;
	private City currentCity;
	private int hotspotRimanenti; // escudendo già il currentCity
	private int tecniciOccupati;
	
	//CODA DEGLI EVENTI
	private PriorityQueue<Event> queue;
	
	public Simulatore(Graph <City,DefaultWeightedEdge> grafo,List<City> cities) {
		
		this.grafo = grafo;
		this.cities=cities;
		
		
	}
	
	public void init(City partenza,int N) {
		this.partenza=partenza;
		this.N=N;
		
		//Inizializza gli output
		this.durata = 0;
		this.revisionati = new ArrayList<Integer>();
		for(int i =0 ;i<N;i++) {
			revisionati.add(i);
		}
		//Inizializzo il modello del mondo
		this.currentCity = this.partenza;
		this.daVisitare = new ArrayList<>(this.cities);
		this.daVisitare.remove(this.currentCity);
		this.hotspotRimanenti = this.currentCity.getnHotspot();
		this.tecniciOccupati = 0;
		
		//caricamento iniziale della coda
		int i=0;
		while(this.tecniciOccupati<N && this.hotspotRimanenti>0) {
			//posso assegnare un tecnico ad un hotspot
			queue.add(new Event(0,EventType.INIZIO_HS,i));
			this.tecniciOccupati++;
			this.hotspotRimanenti--;
		}
		
	}
	
	public void run() {
		
		while(!queue.isEmpty()) {
			Event e = this.queue.poll();
			this.durata = e.getTime();
			processEvent(e);
		}
		
	}

	private void processEvent(Event e) {
		int time = e.getTime();
		EventType type = e.getType();
		int tecnico = e.getTecnico();
		
		switch(type) {
		
		case INIZIO_HS:
			
			this.revisionati.set(tecnico, this.revisionati.get(tecnico)+1);
			
			if(Math.random()<0.1) {
				queue.add(new Event(time+25, EventType.FINE_HS, tecnico));
			}else {
				queue.add(new Event(time+10, EventType.FINE_HS, tecnico));
			}
			
			break;
			
		case FINE_HS :
			
			this.tecniciOccupati--;
			
			if(this.hotspotRimanenti>0) {
				
				//mi sposto su altro hs
				int  spostamento =(int) (Math.random()*11)+10;
				this.tecniciOccupati++;
				this.hotspotRimanenti--;
				queue.add(new Event (time+spostamento,EventType.INIZIO_HS,tecnico));
				
			}else if(this.tecniciOccupati>0){
				
				//non fai nulla,aspetto che tutti finiscano
				
				
			}else if(this.daVisitare.size()>0){
				
				//tutti cambiano quartiere
				City destinazione = piuVicino(this.currentCity,daVisitare);
				int spostamento = (int)(this.grafo.getEdgeWeight(this.grafo.getEdge(currentCity, destinazione))/50.0+60);
				this.currentCity=destinazione;
				this.daVisitare.remove(destinazione);
				this.hotspotRimanenti = this.currentCity.getnHotspot();
				
				this.queue.add(new Event (time+spostamento,EventType.NUOVO_QUARTIERE,-1));
			}else {
				
				//fine simulazione
				
			}
			
			break;
			
		case NUOVO_QUARTIERE :
			
			int i=0;
			while(this.tecniciOccupati<N && this.hotspotRimanenti>0) {
				//posso assegnare un tecnico ad un hotspot
				queue.add(new Event(time,EventType.INIZIO_HS,i));
				this.tecniciOccupati++;
				this.hotspotRimanenti--;
			}
			
			break;
		}
		
	}
	
	public City piuVicino(City currentCity,List<City> vicine){
	
		double min=0.0;
		City destinazione = null;
		for(City c : vicine) {
			double peso= this.grafo.getEdgeWeight(this.grafo.getEdge(currentCity, c));
			if(peso<min) {
				min = peso;
				destinazione = c;
			}	
		}
		return destinazione;
	}
	
	public int getDurata() {
		return durata;
	}

	public List<Integer> getRevisionati() {
		return revisionati;
	}
}
