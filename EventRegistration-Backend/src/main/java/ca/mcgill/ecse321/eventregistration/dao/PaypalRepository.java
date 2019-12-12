package ca.mcgill.ecse321.eventregistration.dao;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.eventregistration.model.Paypal;
import ca.mcgill.ecse321.eventregistration.model.Registration;

public interface PaypalRepository extends CrudRepository<Paypal, String>{
	
	boolean existsByRegistration(Registration registration);
	
	Paypal findByRegistration(Registration registration);
	
	Paypal findByEmail(String email);
	
}
