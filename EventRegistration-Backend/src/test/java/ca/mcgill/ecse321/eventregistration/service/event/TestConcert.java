package ca.mcgill.ecse321.eventregistration.service.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ca.mcgill.ecse321.eventregistration.dao.*;
import ca.mcgill.ecse321.eventregistration.service.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestConcert {

    @Autowired
    private EventRegistrationService service;

    @Autowired
    private ConcertRepository concertRepository;

    @After
    public void clearDatabase() {
        concertRepository.deleteAll();
    }

    @Test
    public void test_01_CreateConcert() {
        assertEquals(0, service.getAllConcerts().size());

        String name = "Bell Center";
        String artist = "Drizzy";

        Calendar c = Calendar.getInstance();
        c.set(2019, Calendar.JANUARY, 18);
        Date concertDate = new Date(c.getTimeInMillis());

        LocalTime startTime = LocalTime.parse("09:00");
        LocalTime endTime = LocalTime.parse("18:00");

        try {
            service.createConcert(name, concertDate, Time.valueOf(startTime) , Time.valueOf(endTime), artist);
        } catch (IllegalArgumentException e) {
            fail();
        }

        checkResultConcert(name, concertDate, startTime, endTime, artist);
    }

    private void checkResultConcert(String name, Date concertDate, LocalTime startTime, LocalTime endTime, String artist) {
        assertEquals(0, service.getAllPersons().size());
        assertEquals(1, service.getAllConcerts().size());
        assertEquals(name, service.getAllConcerts().get(0).getName());
        assertEquals(concertDate.toString(), service.getAllConcerts().get(0).getDate().toString());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        assertEquals(startTime.format(formatter).toString(), service.getAllConcerts().get(0).getStartTime().toString());
        assertEquals(endTime.format(formatter).toString(), service.getAllConcerts().get(0).getEndTime().toString());
        assertEquals(artist, service.getAllConcerts().get(0).getArtist());
        assertEquals(0, service.getAllRegistrations().size());
    }

    @Test
    public void test_02_CreateConcertNull() {
        assertEquals(0, service.getAllRegistrations().size());

        String name = null;
        String artist = null;
        Date concertDate = null;
        Time startTime = null;
        Time endTime = null;

        String error = null;
        try {
            service.createConcert(name, concertDate, startTime, endTime, artist);
        } catch (IllegalArgumentException e) {
            error = e.getMessage();
        }

        // Check error
        assertTrue(error.contains("Event name cannot be empty!"));
        assertTrue(error.contains("Event date cannot be empty!"));
        assertTrue(error.contains("Event start time cannot be empty!"));
        assertTrue(error.contains("Event end time cannot be empty!"));
        // Check model in memory
        assertEquals(0, service.getAllConcerts().size());
    }

    @Test
    public void test_03_CreateConcertNameEmpty() {
        assertEquals(0, service.getAllConcerts().size());

        String name = "";
        String artist = "Lil Yachty";

        Calendar c = Calendar.getInstance();
        c.set(2019, Calendar.JANUARY, 18);
        Date concertDate = new Date(c.getTimeInMillis());

        LocalTime startTime = LocalTime.parse("09:00");
        LocalTime endTime = LocalTime.parse("18:00");

        String error = null;
        try {
            service.createConcert(name, concertDate, Time.valueOf(startTime), Time.valueOf(endTime), artist);
        } catch (IllegalArgumentException e) {
            error = e.getMessage();
        }

        // Check error
        assertEquals("Event name cannot be empty!", error);
        // Check model in memory
        assertEquals(0, service.getAllConcerts().size());
    }

    @Test
    public void test_05_CreateConcertEndTimeBeforeStartTime() {
        assertEquals(0, service.getAllConcerts().size());

        String name = "Coachella";
        String artist = "Rebecca Black";

        Calendar c = Calendar.getInstance();
        c.set(2019, Calendar.MARCH, 7);
        Date concertDate = new Date(c.getTimeInMillis());

        LocalTime startTime = LocalTime.parse("18:00");
        LocalTime endTime = LocalTime.parse("09:00");

        String error = null;
        try {
            service.createConcert(name, concertDate, Time.valueOf(startTime), Time.valueOf(endTime), artist);
        } catch (IllegalArgumentException e) {
            error = e.getMessage();
        }

        // Check error
        assertEquals("Event end time cannot be before event start time!", error);

        // Check model in memory
        assertEquals(0, service.getAllConcerts().size());
    }

    @Test
    public void test_07_CreateConcertArtistSpaces() {
        assertEquals(0, service.getAllConcerts().size());

        String name = "BluesFest";
        String artist = " ";

        Calendar c = Calendar.getInstance();
        c.set(2019, Calendar.NOVEMBER, 22);
        Date concertDate = new Date(c.getTimeInMillis());

        LocalTime startTime = LocalTime.parse("09:00");
        LocalTime endTime = LocalTime.parse("18:00");

        String error = null;
        try {
            service.createConcert(name, concertDate, Time.valueOf(startTime), Time.valueOf(endTime), artist);
        } catch (IllegalArgumentException e) {
            error = e.getMessage();
        }

        // Check error
        assertEquals("Concert artist cannot be empty!", error);
        // Check model in memory
        assertEquals(0, service.getAllConcerts().size());
    }
}
