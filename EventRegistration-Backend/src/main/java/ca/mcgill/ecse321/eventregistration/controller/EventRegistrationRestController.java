package ca.mcgill.ecse321.eventregistration.controller;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ca.mcgill.ecse321.eventregistration.model.*;
import ca.mcgill.ecse321.eventregistration.dto.*;
import ca.mcgill.ecse321.eventregistration.service.EventRegistrationService;

@CrossOrigin(origins = "*")
@RestController
public class EventRegistrationRestController {

	@Autowired
	private EventRegistrationService service;

	// POST Mappings

	// @formatter:off
	// Turning off formatter here to ease comprehension of the sample code by
	// keeping the linebreaks
	// Example REST call:
	// http://localhost:8088/persons/John
	@PostMapping(value = { "/persons/{name}", "/persons/{name}/" })
	public PersonDto createPerson(@PathVariable("name") String name) throws IllegalArgumentException {
		// @formatter:on
		Person person = service.createPerson(name);
		return convertToDto(person);
	}

	// PromoterDto
	@PostMapping(value = { "/promoters/{name}", "/promoters/{name}/" })
	public PromoterDto createPromoter(@PathVariable("name") String name) throws IllegalArgumentException {
		Promoter promoter = service.createPromoter(name);
		return convertToDto(promoter);
	}

	// PaypalDto
	@PostMapping(value = { "/paypal/{email}", "/paypal/{email}/" })
	public PaypalDto createPaypal(@PathVariable("email") String email, @RequestParam int amount)
			throws IllegalArgumentException {
		// @formatter:on
		Paypal paypal = service.createPaypalPay(email, amount);
		return convertToDto(paypal);
	}
	
	// @formatter:off
	// Example REST call:
	// http://localhost:8080/events/testevent?date=2013-10-23&startTime=00:00&endTime=23:59
	// Event Dto, ConcertDto
	@PostMapping(value = { "/events/{name}", "/events/{name}/" })
	public EventDto createEvent(@PathVariable("name") String name, @RequestParam Date date,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME, pattern = "HH:mm") LocalTime startTime,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME, pattern = "HH:mm") LocalTime endTime,
			@RequestParam String artist) throws IllegalArgumentException {
		// @formatter:on
		if(artist == null || artist.trim().length() == 0) {
			//Event event = service.createEvent(name, date, Time.valueOf(startTime), Time.valueOf(endTime));
			Concert event = service.createConcert(name, date, Time.valueOf(startTime), Time.valueOf(endTime), "--");
			return convertToDto(event);
		}
		Concert concert = service.createConcert(name, date, Time.valueOf(startTime), Time.valueOf(endTime), artist);
		return convertToDto(concert);
	}

	// @formatter:off
	@PostMapping(value = { "/register", "/register/" })
	public RegistrationDto registerPersonForEvent(@RequestParam(name = "person") PersonDto pDto,
			@RequestParam(name = "event") EventDto eDto) throws IllegalArgumentException {
		// @formatter:on

		// Both the person and the event are identified by their names
		Person p = service.getPerson(pDto.getName());
		Event e = service.getEvent(eDto.getName());

		Registration r = service.register(p, e);
		return convertToDto(r, p, e);
	}

	// @formatter:off
	@PostMapping(value = { "/promotes", "/promotes/" })
	public PromoterDto assignPromoterForEvent(@RequestParam(name = "promoter") PromoterDto pDto,
			@RequestParam(name = "event") EventDto eDto) throws IllegalArgumentException {
		// @formatter:on

		// Both the promoter and the event are identified by their names
		Promoter p = service.getPromoter(pDto.getName());
		Event e = service.getEvent(eDto.getName());

		List<Event> Events = new ArrayList<Event>();
		for (Event promotes : p.getPromotes()) {
			Events.add(promotes);
		}
		Events.add(e);

		List<Promoter> Promoters = new ArrayList<Promoter>();
		for (Promoter pro : e.getPromoters()) {
			Promoters.add(pro);
		}
		Promoters.add(p);

		p.setPromotes(Events);
		e.setPromoters(Promoters);

		return convertToDto(p);
	}

	// @formatter:off
	@PostMapping(value = { "/paypal", "/paypal/" })
	public RegistrationDto addPaypalToRegistration(@RequestParam(name = "email") String email,
			@RequestParam(name = "amount") String amount, @RequestParam(name = "person") String pName,
			@RequestParam(name = "event") String eName) throws IllegalArgumentException {
		// @formatter:on
		Person p = service.getPerson(pName);
		Event e = service.getEvent(eName);
		Paypal ap = service.createPaypalPay(email, Integer.parseInt(amount));
		Registration reg = service.getRegistrationByPersonAndEvent(p, e);

		reg.setPaypal(ap);
		ap.setRegistration(reg);

		return convertToDto(reg);
	}

	// GET Mappings

	@GetMapping(value = { "/events", "/events/" })
	public List<EventDto> getAllEvents() {
		List<EventDto> eventDtos = new ArrayList<>();
		for (Event event : service.getAllEvents()) {
			eventDtos.add(convertToDto(event));
		}
		return eventDtos;
	}

	// Example REST call:
	// http://localhost:8088/events/person/JohnDoe
	@GetMapping(value = { "/events/person/{name}", "/events/person/{name}/" })
	public List<EventDto> getEventsOfPerson(@PathVariable("name") PersonDto pDto) {
		Person p = convertToDomainObject(pDto);
		return createAttendedEventDtosForPerson(p);
	}

	@GetMapping(value = { "/persons/{name}", "/persons/{name}/" })
	public PersonDto getPersonByName(@PathVariable("name") String name) throws IllegalArgumentException {
		return convertToDto(service.getPerson(name));
	}

	@GetMapping(value = { "/registrations", "/registrations/" })
	public RegistrationDto getRegistration(@RequestParam(name = "person") PersonDto pDto,
			@RequestParam(name = "event") EventDto eDto) throws IllegalArgumentException {
		// Both the person and the event are identified by their names
		Person p = service.getPerson(pDto.getName());
		Event e = service.getEvent(eDto.getName());

		Registration r = service.getRegistrationByPersonAndEvent(p, e);
		return convertToDtoWithoutPerson(r);
	}

	@GetMapping(value = { "/registrations/person/{name}", "/registrations/person/{name}/" })
	public List<RegistrationDto> getRegistrationsForPerson(@PathVariable("name") PersonDto pDto)
			throws IllegalArgumentException {
		// Both the person and the event are identified by their names
		Person p = service.getPerson(pDto.getName());

		return createRegistrationDtosForPerson(p);
	}

	@GetMapping(value = { "/persons", "/persons/" })
	public List<PersonDto> getAllPersons() {
		List<PersonDto> persons = new ArrayList<>();
		for (Person person : service.getAllPersons()) {
			persons.add(convertToDto(person));
		}
		return persons;
	}

	@GetMapping(value = { "/events/{name}", "/events/{name}/" })
	public EventDto getEventByName(@PathVariable("name") String name) throws IllegalArgumentException {
		return convertToDto(service.getEvent(name));
	}

	// Model - DTO conversion methods (not part of the API)

	private EventDto convertToDto(Event e) {
		if (e == null) {
			throw new IllegalArgumentException("There is no such Event!");
		}
		EventDto eventDto = new EventDto(e.getName(), e.getDate(), e.getStartTime(), e.getEndTime());
		if (e instanceof Concert) {
			ConcertDto result = new ConcertDto(eventDto);
			result.setArtist(((Concert)e).getArtist());
			return result;
		}
		return eventDto;
	}

	private ConcertDto convertToDto(Concert c) {
		if (c == null) {
			throw new IllegalArgumentException("There is no such Concert!");
		}
		ConcertDto concertDto = new ConcertDto(c.getName(), c.getDate(), c.getStartTime(), c.getEndTime(),
				c.getArtist());
		return concertDto;
	}

	private PersonDto convertToDto(Person p) {
		if (p == null) {
			throw new IllegalArgumentException("There is no such Person!");
		}
		PersonDto personDto = new PersonDto(p.getName());
		personDto.setEventsAttended(createAttendedEventDtosForPerson(p));
		Set<PaypalDto> pays = new LinkedHashSet<PaypalDto>();
		for(EventDto e : personDto.getEventsAttended()) {
			Registration r = service.getRegistrationByPersonAndEvent(p, service.getEvent(e.getName()));
			if (r.getPaypal() == null) {
				pays.add(new PaypalDto());
			} else {
				Paypal pay = r.getPaypal();
				PaypalDto paypal = new PaypalDto(pay.getEmail(), pay.getAmount());
				pays.add(paypal);
			}
		}
		personDto.setPayments(pays);
		return personDto;
	}

	private PromoterDto convertToDto(Promoter p) {
		if (p == null) {
			throw new IllegalArgumentException("There is no such Promoter!");
		}
		PromoterDto promoterDto = new PromoterDto(p.getName());
		List<EventDto> EventDtos = new ArrayList<EventDto>();
		if(p.getPromotes() != null) {
			for (Event e : p.getPromotes()) {
				EventDtos.add(convertToDto(e));
			}
			promoterDto.setPromotes(EventDtos);
		}

		return promoterDto;
	}

	private PaypalDto convertToDto(Paypal p) {
		if (p == null) {
			throw new IllegalArgumentException("There is no such Paypal!");
		}
		PaypalDto paypalDto = new PaypalDto(p.getEmail(), p.getAmount());
		return paypalDto;
	}

	// DTOs for registrations
	private RegistrationDto convertToDto(Registration r, Person p, Event e) {
		EventDto eDto = convertToDto(e);
		PersonDto pDto = convertToDto(p);
		return new RegistrationDto(pDto, eDto);
	}

	private RegistrationDto convertToDto(Registration r) {
		EventDto eDto = convertToDto(r.getEvent());
		PersonDto pDto = convertToDto(r.getPerson());
		PaypalDto payDto = convertToDto(r.getPaypal());
		RegistrationDto rDto = new RegistrationDto(pDto, eDto);
		rDto.setPaypal(payDto);
		return rDto;
	}

	// return registration dto without person object so that we are not repeating
	// data
	private RegistrationDto convertToDtoWithoutPerson(Registration r) {
		RegistrationDto rDto = convertToDto(r);
		rDto.setPerson(null);
		return rDto;
	}

	private Person convertToDomainObject(PersonDto pDto) {
		List<Person> allPersons = service.getAllPersons();
		for (Person person : allPersons) {
			if (person.getName().equals(pDto.getName())) {
				return person;
			}
		}
		return null;
	}

	// Other extracted methods (not part of the API)

	private List<EventDto> createAttendedEventDtosForPerson(Person p) {
		List<Event> eventsForPerson = service.getEventsAttendedByPerson(p);
		List<EventDto> events = new ArrayList<>();
		for (Event event : eventsForPerson) {
			events.add(convertToDto(event));
		}
		return events;
	}

	private List<RegistrationDto> createRegistrationDtosForPerson(Person p) {
		List<Registration> registrationsForPerson = service.getRegistrationsForPerson(p);
		List<RegistrationDto> registrations = new ArrayList<RegistrationDto>();
		for (Registration r : registrationsForPerson) {
			registrations.add(convertToDtoWithoutPerson(r));
		}
		return registrations;
	}
}
