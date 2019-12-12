package ca.mcgill.ecse321.eventregistration.dto;

public class PaypalDto {
	
	private Integer deviceId;
	private String email;
	private int amount;
	private RegistrationDto registration;

	public PaypalDto() {
	}
	
	public PaypalDto(Integer deviceId) {
		this.deviceId = deviceId;
	}
	
	public PaypalDto(String email, int amount) {
		this.email = email;
		this.amount = amount;
	}
	
	public PaypalDto(String email, int amount, RegistrationDto registration) {
		this.email = email;
		this.amount = amount;
		this.registration = registration;
	}
	
	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	
	public void setEmail(String email) {
		this.email=email;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public int getAmount() {
		return this.amount;
	}
	
	public void setRegistration(RegistrationDto registration) {
		this.registration = registration;
	}
	
	public RegistrationDto getRegistration() {
		return registration;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
