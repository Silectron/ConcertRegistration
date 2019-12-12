package ca.mcgill.ecse321.eventregistration.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Paypal {

	private Integer deviceId;

	private String email;
	
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	
	@Id
	@GeneratedValue
	public Integer getDeviceId() {
		return this.deviceId;
	}
	
	public void setEmail(String email) {
		this.email=email;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	private int amount;
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public int getAmount() {
		return this.amount;
	}

	private Registration registration;
	
	@OneToOne(optional = true)
	@OnDelete(action = OnDeleteAction.CASCADE)
	public Registration getRegistration() {
		return registration;
	}

	public void setRegistration(Registration registration) {
		this.registration = registration;
	}
	
}	
