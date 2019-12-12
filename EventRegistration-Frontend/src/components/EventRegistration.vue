<template>
  <div id="eventregistration">
    <h2>Persons</h2>
    <table id="persons-table">
      <tr>
        <th>Name</th>
        <th>Events</th>
        <th>Payment ID</th>
        <th>Amount ($)</th>
      </tr>
      <tr v-for="(person, i) in persons" v-bind:key="`person-${i}`">
          <td>{{person.name}}</td>
        <td>
          <ul>
            <li v-for="(event, i) in person.eventsAttended" v-bind:key="`event-${i}`">
              <span class='registration-event-name'>{{event.name}}</span>
            </li>
          </ul>
        </td>
        <td>
          <ul>
            <li v-for="(payment, i) in person.payments" v-bind:key="`payment-${i}`">
              <span class='registration-payment-id'>{{payment.email || string}}</span>
            </li>
          </ul>
        </td>
        <td>
          <ul>
            <li v-for="(payment, i) in person.payments" v-bind:key="`payment-${i}`">
              <span class='registration-payment-amount'>{{payment.amount || string}}</span>
            </li>
          </ul>
        </td>
      </tr>
      <tr>
        <td>
          <input id="create-person-person-name" type="text" v-model="newPerson" placeholder="Person Name">
        </td>
        <td>
          <select id="create-person-person-type" v-model="personType">
            <option>Person</option>
            <option>Promoter</option>
          </select>
        </td>
        <td>
          <button id="create-person-button" v-bind:disabled="!newPerson" @click="createPerson(personType, newPerson)">Create Person</button>
        </td>
      </tr>
    </table>
    <span v-if="errorPerson" style="color:red">Error: {{errorPerson}}</span>

    <hr>
    <h2>Events</h2>
    <table id='events-table'>
      <tr>
        <th>Name</th>
        <th>Date</th>
        <th>Start</th>
        <th>End</th>
        <th>Artist</th>
      </tr>
      <tr v-for="(event, i) in events" v-bind:id="event.name" v-bind:key="`event-${i}`">
        <td v-bind:id="`${event.name.replace(/\s/g, '-')}-name`">{{event.name}}</td>
        <td v-bind:id="`${event.name.replace(/\s/g, '-')}-date`">{{event.date}}</td>
        <td v-bind:id="`${event.name.replace(/\s/g, '-')}-starttime`">{{event.startTime}}</td>
        <td v-bind:id="`${event.name.replace(/\s/g, '-')}-endtime`">{{event.endTime}}</td>
        <td v-bind:id="`${event.name.replace(/\s/g, '-')}-artist`">{{event.artist || string}}</td>
      </tr>
      <tr>
        <td>
          <input id="event-name-input" type="text" v-model="newEvent.name" placeholder="Event Name">
        </td>
        <td>
          <input id="event-date-input" type="date" v-model="newEvent.date" placeholder="YYYY-MM-DD">
        </td>
        <td>
          <input id="event-starttime-input" type="time" v-model="newEvent.startTime" placeholder="HH:mm">
        </td>
        <td>
          <input id="event-endtime-input" type="time" v-model="newEvent.endTime" placeholder="HH:mm">
        </td>
        <td>
          <input id="event-artist-input" type="text" v-model="newEvent.artist" placeholder="Artist Name">
        </td>
        <td>
          <button id="event-create-button" v-bind:disabled="!newEvent.name" v-on:click="createEvent(newEvent)">Create</button>
        </td>
      </tr>
    </table>
    <span id="event-error" v-if="errorEvent" style="color:red">Error: {{errorEvent}}</span>

    <hr>
    <h2>Registrations</h2>
      <label>Person:
        <select id='registration-person-select' v-model="selectedPerson">
          <option disabled value="">Please select one</option>
          <option v-for="(person, i) in persons" v-bind:key="`person-${i}`">{{person.name}}</option>
        </select>
      </label>
      <label>Event:
        <select id='registration-event-select' v-model="selectedEvent">
          <option disabled value="">Please select one</option>
          <option v-for="(event, i) in events" v-bind:key="`event-${i}`">{{event.name}}</option>
        </select>
      </label>
      <button id='registration-button' v-bind:disabled="!selectedPerson || !selectedEvent" @click="registerEvent(selectedPerson, selectedEvent)">Register</button>
    <span v-if="errorRegistration" style="color:red">Error: {{errorRegistration}}</span>

    <hr>
    <h2>Assign Professionals</h2>
      <label>Promoter:
        <select id='assign-selected-promoter' v-model="selectedPromoter">
          <option disabled value="">Please select one</option>
          <option v-for="(promoter, i) in promoters" v-bind:key="`promoter-${i}`">{{promoter.name}}</option>
        </select>
      </label>
      <label>Event:
        <select id='assign-selected-event-promoter' v-model="selectedEventPromoter">
          <option disabled value="">Please select one</option>
          <option v-for="(event, i) in events" v-bind:key="`event-${i}`">{{event.name}}</option>
        </select>
      </label>
      <button id='assign-button-promoter' v-bind:disabled="!selectedPromoter || !selectedEventPromoter" @click="assign(selectedPromoter, selectedEventPromoter)">Assign</button>
    <span v-if="errorAssignment" style="color:red">Error: {{errorAssignment}}</span>

    <hr>
    <h2>Pay for Registration with Paypal</h2>
      <label>Person:
        <select id='paypal-person-select' v-model="payment.person">
          <option disabled value="">Please select one</option>
          <option v-for="(person, i) in persons" v-bind:key="`person-${i}`">{{person.name}}</option>
        </select>
      </label>
      <label>Event:
        <select id='paypal-event-select' v-model="payment.event">
          <option disabled value="">Please select one</option>
          <option v-for="(event, i) in getRegisteredEventsByName(payment.person)" v-bind:key="`event-${i}`">{{event.name}}</option>
        </select>
      </label>
      <label>Device Id:
        <input id="paypal-id-input" type="text" v-model="payment.email" placeholder="Email">
      </label>
      <label>Amount:
        <input id="paypal-amount-input" type="text" v-model="payment.amount" placeholder="$">
      </label>
      <button id='paypal-button' v-bind:disabled="!payment.person || !payment.event || !payment.email || !payment.amount" @click="createPaypal(payment.person, payment.event, payment.email, payment.amount)">Make Payment</button>
    <span id='paypal-error' v-if="errorPayment" style="color:red">Error: {{errorPayment}}</span>
    <hr>



  </div>
</template>

<script src="./registration.js"></script>

<style>
#eventregistration {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  background: #f2ece8;
  margin-top: 60px;
}
.registration-event-name {
  display: inline-block;
  width: 25%;
}
.registration-event-name {
  display: inline-block;
}
h1, h2 {
  font-weight: normal;
}
ul {
  list-style-type: none;
  text-align: left;
}
a {
  color: #42b983;
}
</style>
