package ca.mcgill.ecse321.eventregistration.dto;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PersonDto {

	private String name;
	private List<EventDto> eventsAttended;
	private Set<PaypalDto> payments = new LinkedHashSet<PaypalDto>();
	
	public PersonDto() {
	}

	@SuppressWarnings("unchecked")
	public PersonDto(String name) {
		this(name, Collections.EMPTY_LIST);
	}

	public PersonDto(String name, List<EventDto> events) {
		this.name = name;
		this.eventsAttended = events;
	}

	public String getName() {
		return name;
	}

	public List<EventDto> getEventsAttended() {
		return eventsAttended;
	}

	public void setEventsAttended(List<EventDto> events) {
		this.eventsAttended = events;
	}

	public Set<PaypalDto> getPayments() {
		return payments;
	}

	public void setPayments(Set<PaypalDto> payments) {
		this.payments = payments;
	}
}
