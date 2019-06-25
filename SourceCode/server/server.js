/**
 * Rest Server, der ueber eine RestAPI alle Daten fuer die App zur Verfuegung stellt.
 * Bilder und Videos werden statisch bereitgestellt und liegen auf dem Server in Ordnern.
 * Danach wird ein DNS Lookup durchgefuehrt, der die IP Addresse des Servers anhand des Hostnamens
 * herausfindet.
 * Danach wird eine Datenbankverbindung zu einer MongoDB aufgebaut und der Server ist bereit
 * Anfragen von Clients anzunehmen
 */

/**
 * 1. Server mit Expressmodul und MongoClient wird eingerichtet.
 */
//Modul fuer die MongoDB wird von Nodejs Package geholt
let mongoClient = require('mongodb').MongoClient;
//Die Url fuer die Mongodatenbank wird erstellt
let mongoUrl = "mongodb://geilerficker.chickenkiller.com:27017/";
//Dieses Modul von Nodejs wird gebraucht, um die IP Addresse anzuzeigen
let dns = require('dns');
//Express ist das Modul, welches die RestAnfragen entgegennimmt und verarbeitet
let express = require('express');
let app = express();
//Hier wird der Server vom Modul "http" geholt, und gestartet
require('http').createServer();
//Hier wird der Port fuer die App gesetzt
let port = process.env.PORT || 3000;
//Wird genutzt, um das HTML in JSON umzuwandeln, damit Nodejs mit den Daten arbeiten kann.
var bodyParser = require("body-parser");

//dataloader
let path = require('path');

//Bodyparser wird so eingerichtet, dass er den String aus dem HTML Body in JSON umwandelt.
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

/**
 * 2. Statische Links werden von der Expressapp bereitgestellt
 */
app.use("/logos", express.static(path.join(__dirname, '/logos')));
app.use("/pictures", express.static(path.join(__dirname, '/pictures')));
app.use("/videos", express.static(path.join(__dirname, '/videos')));

/**
 * IP-Adresse wird herausgefunden (diese wird auf der Konsole angezeigt und wird in die App eingetragen)
 */
dns.lookup(require('os').hostname(), function (err, ip) {
    let setDatabase = function (err, db) {
        if (err) throw err;
        try {
            let dbo = db.db("CampusfestApp");
            /**
             * 5. Server ist bereit fuer Anfragen fuer Clients
             */
            app.use("/datatoload", (req, res) => {
                dbo.collection("DataToLoad").find({}).toArray(function (err, dataToLoad) {
                    if (err) throw err;
                    if(dataToLoad) {
                        res.send({collection: "DataToLoad", data: dataToLoad});
                    }else{
                        res.sendStatus(500)
                    }
                });
            });
            app.use("/artists", (req, res) => {
                dbo.collection("Artists").find({}).toArray(function (err, bands) {
                    if (err) throw err;
                    if(bands){
                        res.send({collection: "Artists", data: bands});
                    }else{
                        res.sendStatus(500)
                    }
                });
            });
            app.use("/timetables", (req, res) => {
                //Holt alle Timetables aus der Datenbank und fuehre die untere Funktion aus
                dbo.collection("Timetables").find({}).toArray(function (err, timetables) {
                    if (err) throw err;
                    if(timetables){
                        res.send({collection: "Timetables", data: timetables});
                    }else{
                        res.sendStatus(500)
                    }
                });
            });
            app.use("/stages", (req, res) => {
                dbo.collection("Stages").find({}).toArray(function (err, stages) {
                    if (err) throw err;
                    if(stages) {
                        res.send({collection: "Stages", data: stages});
                    }else{
                        res.sendStatus(500)
                    }
                });
            });
            console.log("Database connected");
            console.log("Server startet on: http://" + ip + ":" + port)
        } catch (e) {
            console.log(e)
        }
    }
    /**
     * 4. Verbindung zur MongoDB wird aufgebaut.
     */
    mongoClient.connect(mongoUrl, setDatabase);
});
app.listen(port);
