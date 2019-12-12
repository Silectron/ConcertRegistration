package ca.mcgill.ecse321.eventregistration.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;

import java.util.List;
import javax.persistence.ManyToMany;

@Entity
public class Promoter extends Person{
	
	private List<Event> promotes;

	@ManyToMany(fetch = FetchType.EAGER)
	public List<Event> getPromotes(){
		return promotes;
	}
	public void setPromotes(List<Event> promotess) {
		promotes=promotess;
	}
	
}
