package it.polito.tdp.nyc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.nyc.db.NYCDao;

public class Model {
	
	private List<String > providers;
	private List<City> cities;
	private Graph <City,DefaultWeightedEdge> grafo;
	
	//RISULTATI per simulazione
	private int durata;
	private List<Integer> revisionati;
	
	public List<String > getProviders(){
		if(this.providers==null) {
			NYCDao dao = new NYCDao();
			this.providers= dao.getProvider();
		}
		return this.providers;
	}
	
	
	public String creaGrafo(String provider) {
		
		NYCDao dao = new NYCDao();
		this.cities = dao.getCity(provider);
		
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(this.grafo, this.cities);
		
		for(City c1 : cities) {
			for(City c2 : this.cities) {
				if(!c1.equals(c2)) {
					double peso = LatLngTool.distance(c1.getPosizione(), c2.getPosizione(),LengthUnit.KILOMETER);
					Graphs.addEdge(this.grafo, c1, c2, peso);
				}
			}
		}
		
		return "GRAFO CREATO \n #VERTICI : "+this.grafo.vertexSet().size()+"\n #ARCHI : "+this.grafo.edgeSet().size();
		
	}
	
	public List<City> getCities(){
		return this.cities;
	}
	
	public List <CityDistance> getCityDistances(City scelto){
		
		List <CityDistance> result = new ArrayList<>();
		List <City> vicini =  Graphs.neighborListOf(this.grafo, scelto);
		
		for(City c : vicini) {
			result.add(new CityDistance(c.getNome(), this.grafo.getEdgeWeight(this.grafo.getEdge(scelto, c))));
		}
		
		Collections.sort(result,new Comparator <CityDistance>(){
			
			public int compare(CityDistance  c1, CityDistance c2) {
				return c1.getDistanza().compareTo(c2.getDistanza());
			}
		});
		
		return result; 
	}
	
	public void simula(City scelto, int N) {
		Simulatore sim = new Simulatore(this.grafo, this.cities);
		sim.init(scelto, N);
		sim.run();
		this.durata = sim.getDurata();
		this.revisionati = sim.getRevisionati();
	}
}
