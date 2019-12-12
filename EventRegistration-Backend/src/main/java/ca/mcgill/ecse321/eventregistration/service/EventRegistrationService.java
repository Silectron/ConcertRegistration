package ca.mcgill.ecse321.eventregistration.service;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.mcgill.ecse321.eventregistration.dao.*;
import ca.mcgill.ecse321.eventregistration.model.*;

@Service
public class EventRegistrationService {

	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private PersonRepository personRepository;
	@Autowired
	private RegistrationRepository registrationRepository;
	@Autowired
	private ConcertRepository concertRepository;
	@Autowired
	private PaypalRepository paypalRepository;
	@Autowired
	private PromoterRepository promoterRepository;

	@Transactional
	public Person createPerson(String name) {
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("Person name cannot be empty!");
		} else if (personRepository.existsById(name)) {
			throw new IllegalArgumentException("Person has already been created!");
		}
		Person person = new Person();
		person.setName(name);
		personRepository.save(person);
		return person;
	}


	@Transactional
	public Person getPerson(String name) {
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("Person name cannot be empty!");
		}
		Person person = personRepository.findByName(name);
		return person;
	}

	@Transactional
	public List<Person> getAllPersons() {
		return toList(personRepository.findAll());
	}

	@Transactional
	public Event buildEvent(Event event, String name, Date date, Time startTime, Time endTime) {
		// Input validation
		String error = "";
		if (name == null || name.trim().length() == 0) {
			error = error + "Event name cannot be empty! ";
		} else if (eventRepository.existsById(name)) {
			throw new IllegalArgumentException("Event has already been created!");
		}
		if (date == null) {
			error = error + "Event date cannot be empty! ";
		}
		if (startTime == null) {
			error = error + "Event start time cannot be empty! ";
		}
		if (endTime == null) {
			error = error + "Event end time cannot be empty! ";
		}
		if (endTime != null && startTime != null && endTime.before(startTime)) {
			error = error + "Event end time cannot be before event start time!";
		}
		error = error.trim();
		if (error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		event.setName(name);
		event.setDate(date);
		event.setStartTime(startTime);
		event.setEndTime(endTime);
		return event;
	}

	@Transactional
	public Event createEvent(String name, Date date, Time startTime, Time endTime) {
		Event event = new Event();
		buildEvent(event, name, date, startTime, endTime);
		eventRepository.save(event);
		return event;
	}

	@Transactional
	public Event getEvent(String name) {
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("Event name cannot be empty!");
		}
		Event event = eventRepository.findByName(name);
		return event;
	}

	// This returns all objects of instance "Event" (Subclasses are filtered out)
	@Transactional
	public List<Event> getAllEvents() {
		return toList(eventRepository.findAll());//.stream().filter(e -> e.getClass().equals(Event.class)).collect(Collectors.toList());
	}

	@Transactional
	public Registration register(Person person, Event event) {
		String error = "";
		if (person == null) {
			error = error + "Person needs to be selected for registration! ";
		} else if (!personRepository.existsById(person.getName())) {
			error = error + "Person does not exist! ";
		}
		if (event == null) {
			error = error + "Event needs to be selected for registration!";
		} else if (!eventRepository.existsById(event.getName())) {
			error = error + "Event does not exist!";
		}
		if (registrationRepository.existsByPersonAndEvent(person, event)) {
			error = error + "Person is already registered to this event!";
		}

		error = error.trim();

		if (error.length() > 0) {
			throw new IllegalArgumentException(error);
		}

		Registration registration = new Registration();
		registration.setId(person.getName().hashCode() * event.getName().hashCode());
		registration.setPerson(person);
		registration.setEvent(event);

		registrationRepository.save(registration);

		return registration;
	}

	@Transactional
	public List<Registration> getAllRegistrations() {
		return toList(registrationRepository.findAll());
	}

	@Transactional
	public Registration getRegistrationByPersonAndEvent(Person person, Event event) {
		if (person == null || event == null) {
			throw new IllegalArgumentException("Person or Event cannot be null!");
		}

		return registrationRepository.findByPersonAndEvent(person, event);
	}
	@Transactional
	public List<Registration> getRegistrationsForPerson(Person person){
		if(person == null) {
			throw new IllegalArgumentException("Person cannot be null!");
		}
		return registrationRepository.findByPerson(person);
	}

	@Transactional
	public List<Registration> getRegistrationsByPerson(Person person) {
		return toList(registrationRepository.findByPerson(person));
	}

	@Transactional
	public List<Event> getEventsAttendedByPerson(Person person) {
		if (person == null) {
			throw new IllegalArgumentException("Person cannot be null!");
		}
		List<Event> eventsAttendedByPerson = new ArrayList<>();
		for (Registration r : registrationRepository.findByPerson(person)) {
			eventsAttendedByPerson.add(r.getEvent());
		}
		return eventsAttendedByPerson;
	}

	private <T> List<T> toList(Iterable<T> iterable) {
		List<T> resultList = new ArrayList<T>();
		for (T t : iterable) {
			resultList.add(t);
		}
		return resultList;
	}
	
	@Transactional
	public List<Concert> getAllConcerts(){
		return toList(concertRepository.findAll());
	}
	
	@Transactional
	public Concert createConcert(String name, Date date, Time startTime, Time endTime, String artist) {
		String error = "";
		if (name == null || name.trim() == "") {
			error = error + "Event name cannot be empty! ";
		}
		if (date == null) {
			error = error + "Event date cannot be empty!";
		} 
		if(startTime == null) {
			error = error + "Event start time cannot be empty!";
		}
		if(endTime == null) {
			error = error + "Event end time cannot be empty!";
		} 
		if (artist == null || artist == "" || artist == " ") {
			error = error + "Concert artist cannot be empty!";
		}
		error = error.trim();

		if (error.length() > 0) {
			throw new IllegalArgumentException(error);
		}

		if(startTime.after(endTime)) {
			throw new IllegalArgumentException("Event end time cannot be before event start time!");
		}
		for (Event e : eventRepository.findAll()) {
			if(e.getName().equals(name)) {
				throw new IllegalArgumentException("Event has already been created!");
			}
		}
		
		Concert c = new Concert();
		c.setName(name);
		c.setDate(date);
		c.setStartTime(startTime);
		c.setEndTime(endTime);
		c.setArtist(artist);
		eventRepository.save(c);
		concertRepository.save(c);
	
		return c;
	}
	
	@Transactional
	public Paypal createPaypalPay(String email, Integer amount) {
		//boolean paypalExists = false;
		String error = "";
		
		if ((email == null  || amount == null || email.trim().length() < 1)) {
			error = error + "Email is null or has wrong format!";
		}
		else {
			Pattern reg = Pattern
				.compile("^([0-9a-zA-Z]([-\\.\\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\\w]*[0-9a-zA-Z]\\.)+[a-zA-Z]{2,9})$");
			Matcher m = reg.matcher(email);
			if(!m.matches()) {
				error = error + "Email is null or has wrong format!";
			}
		}
		if(amount < 0) {
			error = error + "Payment amount cannot be negative!";
		}
		error = error.trim();

		if (error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
		
		if (email.contains(" ")) throw new IllegalArgumentException("Email may not contain white spaces.");
		
		Paypal p = new Paypal();
		p.setEmail(email);
		p.setAmount(amount);
		paypalRepository.save(p);
		
		return p;
	}
	
	@Transactional
	public void pay(Registration r, Paypal ap) {
		boolean registrationExists = false;
		boolean paypalExists = false;
		
		if(r == null || ap == null) {
			throw new IllegalArgumentException("Registration and payment cannot be null!");
		}
		
		for(Registration reg : registrationRepository.findAll()) {
			if(r.getId() == reg.getId()) {
				registrationExists = true;
				break;
			}
		}
		
		for(Paypal p : paypalRepository.findAll()) {
			if(p.getEmail().equals(ap.getEmail())) {
				paypalExists = true;
				break;
			}
		}
		
		if(!registrationExists) {
			throw new IllegalArgumentException("Registration does not exist.");
		}
		if(!paypalExists) {
			throw new IllegalArgumentException("Paypal does not exist.");
		}
		
		r.setPaypal(ap);
		ap.setRegistration(r);	
		
		registrationRepository.save(r);
		paypalRepository.save(ap);
	}
	
	@Transactional
	public Promoter createPromoter(String name) {
		if(name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("Promoter name cannot be empty!");
		} else if(promoterRepository.existsById(name)) {
			throw new IllegalArgumentException("Promoter has already been created!");
		}
		
		Promoter p = new Promoter();
		p.setName(name);
		promoterRepository.save(p);
		
		return p;
	}
	
	@Transactional
	public void promotesEvent(Promoter p, Event e) {
		boolean promoterExists = false;
		boolean eventExists = false;
		if(e == null) {
			throw new IllegalArgumentException("Event must be specified.");
		}
		for(Promoter pro : promoterRepository.findAll()) {
			if(pro.getName().equals(p.getName())) {
				promoterExists = true;
			}
		}
		for(Event eve : eventRepository.findAll()) {
			if(eve.getName().equals(e.getName())) {
				eventExists = true;
			}
		}
		
		if(!promoterExists) {
			throw new IllegalArgumentException("Promoter needs to be selected for promotes!");
		}
		if(!eventExists) {
			throw new IllegalArgumentException("Event does not exist!");
		}
		
		List<Event> promotedEvents = new ArrayList<>();
		if(p.getPromotes() != null) {
			promotedEvents = p.getPromotes();
		}
		promotedEvents.add(e);
		
		p.setPromotes(promotedEvents);	
		promoterRepository.save(p);
	}
	
	@Transactional
	public List<Promoter> getAllPromoters() {
		return toList(promoterRepository.findAll());
	}
	
	@Transactional
	public Promoter getPromoter(String name) {
		if(name == null || name == "" || name == " ") {
			throw new IllegalArgumentException("Person name cannot be empty!");
		}
		
		for(Promoter p : promoterRepository.findAll()) {
			if(p.getName().equals(name)) {
				return p;
			}
		}
		
		throw new IllegalArgumentException("A promoter with the specified name does not exist.");
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
