package ca.mcgill.ecse321.eventregistration.dto;

public class RegistrationDto {

	private PersonDto person;
	private EventDto event;
	private PaypalDto paypal;

	public RegistrationDto() {
	}

	public RegistrationDto(PersonDto person, EventDto event) {
		this.person = person;
		this.event = event;
	}
	
	public RegistrationDto(PersonDto person, EventDto event, PaypalDto paypal) {
		this.person = person;
		this.event = event;
		this.paypal = paypal;
	}

	public EventDto getEvent() {
		return event;
	}

	public void setEvent(EventDto event) {
		this.event = event;
	}

	public PersonDto getPerson() {
		return person;
	}

	public void setPerson(PersonDto person) {
		this.person = person;
	}
	
	public PaypalDto getPaypal() {
		return paypal;
	}
	
	public void setPaypal(PaypalDto paypal) {
		this.paypal = paypal;
	}
}
