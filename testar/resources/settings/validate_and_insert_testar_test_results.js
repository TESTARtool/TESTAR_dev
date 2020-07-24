"use strict";

const path = require('path');
const MongoClient = require('mongodb').MongoClient;
const assert = require ('assert');
var Ajv = require('ajv');
const fs = require ('fs');

validate(process.argv[2], process.argv[3])

var pkmAddress = process.argv[4];
var pkmPort = process.argv[5];
const url = 'mongodb://' + pkmAddress + ':' + pkmPort;
const dbName = 'mydb';
const client = new MongoClient(url,{ useUnifiedTopology: true});

function validate(schema, data) {
		
	if(schema == null || !schema.includes(".json")){ 
		throw 'First argument must be json schema';
	}
	if(data == null || !data.includes(".json")){
		throw 'Second argument must be json data';
	}
		
	let rawschema = fs.readFileSync(schema);
	let documentschema = JSON.parse(rawschema);
		
	let rawdata = fs.readFileSync(data);
	let documentdata = JSON.parse(rawdata);
		
	const ajv = new Ajv({ allErrors: true });
	var validate = ajv.compile(documentschema);
	var valid = validate(documentdata);
	if (!valid){
		console.log(validate.errors);
		throw (validate.errors);
	}
}

client.connect(function(err) {
	//Prepare db name and collection name
	assert.equal(null,err);
	const db = client.db(dbName);
	const collection = db.collection('TESTAR_Test_Results');
	const val = process.argv[3];
	
	//Prepare document
	let rawdocument = fs.readFileSync(val);
	let document = JSON.parse(rawdocument);
	
	//Verify that this document doesnt exists in the collection
	console.log("Verifying that this artefact doesnt exists...");
	collection.findOne(document, function(err, count) {
		if (count){
			console.log("ERROR: This artefact already exists, maybe you want to update them");
			client.close();
		}
		else {
			console.log("OK!");
			collection.insertOne(document, function(err, res) {
				if (err) {
					console.log(err);
					throw err;
				}
				else {
					console.log("TESTAR Test Results document inserted: " + val);
					console.log("TestResultsArtefactId: " + document._id);
					client.close();
				}
			});
		}
	});
});
