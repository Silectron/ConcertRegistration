package ca.mcgill.ecse321.eventregistration.dto;

import java.util.List;

public class PromoterDto extends PersonDto{
	
	private List<EventDto> promotes;
	
	public PromoterDto() {
	}
	
	public PromoterDto(String name) {
		super(name);
	}
	
	public PromoterDto(String name, List<EventDto> promotes) {
		super(name);
		this.promotes = promotes;
	}
	
	public void setPromotes(List<EventDto> promotes) {
		this.promotes=promotes;
	}

	public List<EventDto> getPromotes(){
		return promotes;
	}
	
}
