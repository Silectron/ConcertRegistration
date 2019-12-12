package ca.mcgill.ecse321.eventregistration.dto;

import java.sql.Date;
import java.sql.Time;

public class ConcertDto extends EventDto{
	
	private String artist;
	
	public ConcertDto() {
	}

	public ConcertDto(String name, Date date, Time startTime, Time endTime, String artist) {
		super(name, date, startTime, endTime);
		this.artist = artist;
	}
	
	public ConcertDto(EventDto e) {
		super(e.getName(),e.getDate(),e.getStartTime(),e.getEndTime());
	}
	
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	public String getArtist() {
		return this.artist;
	}
}
