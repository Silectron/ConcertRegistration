import _ from 'lodash';
import axios from 'axios';
let config = require('../../config');

let backendConfigurer = function () {
  switch (process.env.NODE_ENV) {
    case 'testing':
    case 'development':
      return 'http://' + config.dev.backendHost + ':' + config.dev.backendPort;
    case 'production':
      return 'https://' + config.build.backendHost + ':' + config.build.backendPort;
  }
}

let backendUrl = backendConfigurer();

let AXIOS = axios.create({
  baseURL: backendUrl
  // headers: {'Access-Control-Allow-Origin': frontendUrl}
});

export default {
  name: 'eventregistration',
  data() {
    return {
      persons: [],
      promoters: [],
      events: [],
      concerts: [],
      newPerson: '',
      personType: 'Person',
      newEvent: {
        name: '',
        date: '2017-12-08',
        startTime: '09:00',
        endTime: '11:00',
        artist: ''
      },
      selectedPerson: '',
      selectedPromoter: '',
      selectedEvent: '',
      selectedEventPromoter: '',
      payment: {
        email: '',
        amount: '',
        person: '',
        event: ''
      },
      errorPerson: '',
      errorPromoter: '',
      errorEvent: '',
      errorAssignment: '',
      errorPayment: '',
      errorRegistration: '',
      response: [],
      
      string: "--",
    }
  },
  created: function () {
    // Initializing persons
    AXIOS.get('/persons')
    .then(response => {
      this.persons = response.data;
      this.persons.forEach(person => {
        this.getRegistrations(person.name);
        person.payments.forEach(payment =>{
          if(!payment.email) {
            payment.email = "--";
            payment.amount = "--";
          }
        })
      })
    })
    .catch(e => {this.errorPerson = e});

    AXIOS.get('/events').then(response => {
      this.events = response.data
    })
    .catch(e => {this.errorEvent = e});
  
  },

  methods: {
    createPerson: function (personType, personName) {
      if(personType === "Person") {
        AXIOS.post('/persons/'.concat(personName), {}, {})
        .then(response => {
          this.persons.push(response.data);
          this.errorPerson = '';
          this.newPerson = '';
        })
        .catch(e => {
          e = e.response.data.message ? e.response.data.message : e;
          this.errorPerson = e;
          console.log(e);
        });
      } else if(personType === "Promoter") {
        AXIOS.post('promoters/'.concat(personName), {}, {})
        .then(response => {
          this.persons.push(response.data);
          this.promoters.push(response.data);
          this.errorPerson = '';
          this.newPerson = '';
        })
        .catch(e => {
          e = e.response.data.message ? e.response.data.message : e;
          this.errorPerson = e;
          console.log(e);
        });
      }
    },

    createEvent: function (newEvent) {
      let url = '';
        AXIOS.post('/events/'.concat(newEvent.name), {}, {params: newEvent})
        .then(response => {
          this.events.push(response.data);
          this.errorEvent = '';
          this.newEvent.artist = '';
          this.newEvent.name = '';
        })
        .catch(e => {
          e = e.response.data.message ? e.response.data.message : e;
          this.errorEvent = e;
          console.log(e);
        });
    },

    getRegisteredEventsByName: function(personName) {
      if(personName != "") {
        return this.persons.find(x => x.name === personName).eventsAttended;
      }
    },

    getRegistrations: function (personName) {
      AXIOS.get('/events/person/'.concat(personName))
      .then(response => {
        if (!response.data || response.data.length <= 0) return;

        let index = this.persons.map(x => x.name).indexOf(personName);
        this.persons[index].eventsAttended = [];
        response.data.forEach(event => {
          this.persons[index].eventsAttended.push(event);
        });
      })
      .catch(e => {
        e = e.response.data.message ? e.response.data.message : e;
        console.log(e);
      });
    },

    registerEvent: function (personName, eventName) {
      let event = this.events.find(x => x.name === eventName);
      let person = this.persons.find(x => x.name === personName);
      let params = {
        person: person.name,
        event: event.name
      };

      AXIOS.post('/register', {}, {params: params})
      .then(response => {
        person.eventsAttended.push(event)
        this.selectedPerson = '';
        this.selectedEvent = '';
        this.errorRegistration = '';
      })
      .catch(e => {
        e = e.response.data.message ? e.response.data.message : e;
        this.errorRegistration = e;
        console.log(e);
      });
    },

    assign: function(promoter, event) {
      let p = this.promoters.find(x => x.name === promoter)
      let e = this.events.find(x => x.name === event)
      let params = {
        promoter: p.name,
        event: e.name
      }
      AXIOS.post(`/promotes`, {}, {params: params})
        .then(response => {
          this.selectedPromoter = '';
          this.selectedEventPromoter = '';
          this.errorAssignment = '';
        }) 
        .catch(e => {
          e = e.response.data.message ? e.response.data.message : e;
          this.errorAssignment = e;
          console.log(e);
        });
    },

    createPaypal: function (personName, eventName, email, amount) {
      let e = this.events.find(x => x.name === eventName)
      let person = this.persons.find(x => x.name === personName)
      let params = {
        email: email,
        amount: amount,
        person: person.name,
        event: e.name
      }
      AXIOS.post(`/paypal`, {}, {params : params})
        .then(response => {
          var i;
          for(i = 0; i < person.eventsAttended.length; i++) {
            if(person.eventsAttended[i].name === eventName) break;
          }

          if(person.payments[0] != null) {
            person.payments[i].email = response.data.paypal.email
            person.payments[i].amount = response.data.paypal.amount
          } else {    
            person.payments.push(response.data.paypal)
          }

          this.payment.email = "";
          this.payment.amount = "";
          this.errorPayment='' 
        })
        .catch(e => {
          e = e.response.data.message ? e.response.data.message : e;
          this.errorPayment = e;
          console.log(e);
        })
    }
  }
}
