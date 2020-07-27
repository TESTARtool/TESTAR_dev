"use strict";

const MongoClient = require('mongodb').MongoClient;
const assert = require ('assert');
const fs = require ('fs');

// node find_in_testar_state_model.js 10.101.0.224 49161 stateModelAppName MyThaiStar

// node find_in_testar_state_model.js 10.101.0.224 49161 stateModelAppName MyThaiStar _id

var pkmAddress = process.argv[2];
var pkmPort = process.argv[3];
var queryKey = process.argv[4];
var queryValue = process.argv[5];

validate(pkmAddress, pkmPort, queryKey, queryValue)

const url = 'mongodb://' + pkmAddress + ':' + pkmPort;
const dbName = 'mydb';
const client = new MongoClient(url,{ useUnifiedTopology: true});

function validate(pkmAddress, pkmPort, queryKey, queryValue){
	//TODO: Validate pkmAddress string is a valid IP address
	if(pkmAddress == null){  
		throw 'First argument must be the PKM IP address';
	}
	//TODO: Validate pkmPort string is a valid port
	if(pkmPort == null){ 
		throw 'Second argument must be the PKM port';
	}
	if(queryKey == null){ 
		throw 'Third argument must be the Key name of the object to query';
	}
	if(queryValue == null){ 
		throw 'Fourth argument must be the Value of the object to query';
	}
}

client.connect(function(err) {
	if (err) throw err;
	
	// Prepare the connection to the State Model Collection
	const db = client.db(dbName);
	const collection = db.collection('TESTAR_State_Model');
	
	// Prepare the query to find the desired document or property, using regex for the object value
	var query = {};
	var regexValue = RegExp(".*" + queryValue + ".*");
	query[queryKey] = regexValue;
	
	if(process.argv[6] == null) {
		// Return all the content of the documents for the matching query
		// node find_in_testar_state_model.js 10.101.0.224 49161 stateModelAppName MyThaiStar
		collection.find(query).toArray(function(err, result) {
			if (err) throw err;
			console.log(result);
			client.close();
		});
	} else {
		// Return non-duplicates concrete properties of the documents for the matching query
		// node find_in_testar_state_model.js 10.101.0.224 49161 stateModelAppName MyThaiStar _id
		collection.distinct(process.argv[6], query, function (err, property) {
			if (err) throw err;
			console.log(JSON.stringify(property));
			client.close();
		});
	}

});
