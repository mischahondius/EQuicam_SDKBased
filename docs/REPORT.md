# EQuicam App
Mischa Hondius, 6053017.

**Kort:** live bekijken van videobeelden + opnemen en terugkijken van deze beelden.

De EQuicam App maakt het mogelijk om live videobeelden van een EQuicam (ip camera) te bekijken en op te nemen op je Android apparaat. De EQuicam is de eerste videocamera die volledig automatisch paard en ruiter in beeld brengt. De camera zoomt, pant en tilt zelf mee op basis van beeldherkenning. Hierbij is dus geen "tracking device" nodig. De combinatie EQuicam + app biedt ruiters en trainers binnen de hippische sport de mogelijkheid tot het gemakkelijk opnemen en analyseren van trainingen, zonder dat daarbij een dure cameraman aan te pas hoeft te komen.

**Functionaliteit**
- Verbinden met EQuicam
- Live weergeven beeld camera
- Opname live beeld
- Terugkijken opgenomen clips

Bovenstaande functionaliteit heb ik succesvol weten te implementeren. Ik heb gekozen om meer tijd te steken in design, gebruiksvriendelijkheid en het voorkomen van crashes bij cornercases. Hierdoor ben ik niet toe gekomen aan optionele functionaliteit zoals hieronder beschreven. Het minimum viable product staat naar mijn idee echter als een huis. Onderstaande ga ik graag in mijn vrije tijd toevoegen na de studie.

**Optionele functionaliteit**
- Zoekfunctie opgenomen clips
- Aanpassen metadata clips
- Zelf meerdere camera's toevoegen/verwijderen/bekijken
- Opnemen van commentaar trainer live of achteraf, externe mic of intern telefoon
- Accounts, met eigen camera's daaronder
- Favorieten (clips)


**Schermen**

De App zal bestaan uit slechts 2 schermen:
1. LIVE
2. CLIPS

Ik ben afgeweken van bovenstaande op de volgende manier: er is één extra scherm toegevoegd: "Camera". Hier kan men de standaard camera url aanpassen, om zo een eigen camera te gebruiken met de app. In principe kan iedere IP camera/Webcam die een RTSP stream uitgeeft, met of zonder gebruikersnaam en wachtwoord, worden gebruikt met deze app. In eerste instantie wilde ik de cameraurl hardcoden voor de klant. Het is even de vraag of dit scherm ook daadwerkelijk zichbaar blijft voor de eindgebruiker in de toekomst, of dat deze slechts zichtbaar is voor bepaalde accounts (Admins). 

**1. LIVE**
Op dit scherm kan de gebruiker het live beeld van de camera bekijken. Om ongewenst dataverbruik te voorkomen dient te gebruiker doelbewust een verbinding te maken met de camera. Dit door middel van de tekst "Tik op het scherm om verbinding te maken met uw EQuicam" weergeven. Een seperate knop lijkt mij hiervoor overbodig. Deze beslissing is onveranderd gebleven sinds het design. Ik ben voor simplicity gegaan: het live scherm bevat slechts 2 elementen: Een videoscherm en een opnameknop. Dit op het voor ruiters zo makkelijk mogelijk te houden. Men kan kijken en opnemen. Meer niet.  

Tijdens het verbinden krijgt de gebruiker feedback in de vorm van de tekst "Verbinden...", wat bevestigt aan gebruiker dat er daadwerkelijk iets gebeurt. Hieraan heb ik nog een native android laad icoontje toegevoegd. Dit bevestigd de gebruiker dat er daadwerkelijk gewerkt wordt aan het leggen van een verbinding met de camera. Na dat er een verbinding is gelegd wordt in het videovenster live beeld weergegeven. 

Er is voor gekozen om de opname knop te verbergen tot op het moment dat er daadwerkelijk verbinding is met de camera. Dit om te voorkomen dat er lege opnames worden gemaakt. 

Door middel van de grote rode opvallende opnameknop kan er een opname worden gestart. Opname wordt bevestigt op meerdere manieren:
- 1. Toast message
- 2. Gloeiende opnameknop
- 3. Opname duratie, er gaat een timer lopen.

Stop opname wordt door het ongedaanmaken van bovenstaande bevestigt. De Toast-message "Opname Gestopt". Alle beslissingen van het DesignDoc zijn in stand gebleven. 

**2. CLIPS**
Een scherm waarbij een lijst van video's wordt weergegeven die met deze App zijn opgenomen. Een lijst met video's inclusief thumbnails wordt weergegeven. Er is afgeweken van het plan om een standaard videoplayer in beeld te houden bovenaan. Dit om zoveel mogelijk clips weer te kunnen geven in dit venster. Aangezien er nog een zoekfunctie ontbreekt is het voor de gebruiker fijn als er in één oogopslag zo veel mogelijk video's in beeld zijn. Dit maakt het makkelijker om de gewenste opname terug te vinden. Daarnaast wil men de video's terugkijken in fullscreen, aangezien er op klein scherm weinig te analyseren valt. Bij een klik op een clip wordt dan ook direct de full screen native player geladen. Bij het draaien naar landscape heeft de gebruiker al helemaal een full screen beeld. 

NAVIGATIE
- Doel is om 2 tabjes onder de actionbar weer te geven, mogelijkheid tot swipen tussen de twee hoofdschermen.

**API/SDK's/Android functies**
- De opname wordt gedaan op basis van een SDK van VideoExpertsGroup. De instabiele codec van een RTSP stream is zeer moeilijk handelbaar. Dat is voor mij niet realistisch om op korte termijn zelf te schrijven. Zover ik kan zien is dit de enige partij die hier een serieuze oplossing voor heeft geschreven. 
-ActionBar tabs
-Android MediaPlayer voor afspelen opgenomen MP4's. (Evt. Alsnog SDK videoplayer ivm evt instabiliteit videostreams).

**Data**
- Hardcoded RTSP Stream url voor verbinding met Camera (kan later aanpasbaar worden en bijv. worden opgeslagen in SharedPrefs of gekoppeld aan een user account opgeslagen in een online database)
- Opslag van MP4 clips in lokaal geheugen, DCIM/EQuicam Clips -> Unieke filenames op basis van datum en tijd
- Evt. opslag metadata bij video's -> in SharedPrefs, koppeling op basis bestandsnaam?

**Functies**

//Is Playing/Is Recording checks

private boolean						is_record

private boolean 					playing

Het bleek inderdaad handig, noodzakelijk zelfs, om bij te houden of er werd opgenomen en of afgespeeld. Dit om te voorkomen dat een opname dubbel wordt ingestart. Ook kon hierdoor worden tegengegaan dat gebruikers van scherm wisselen tijdens opname, wat zeer ongewenst is. De app zou hierdoor ook crashen. Als er bijvoorbeeld tijdens opname naar "Clips" gegaan zou worden, zou ClipsActivity trachten een nieuwe clip aan te maken van de video die op dat moment nog "in de maak" is. Dit trekt de functie niet die de thumbnails maakt. Ook metadata kan nog niet worden uitgelezen. Heeft de App moeite mee. Deze cornercase is nu voorkomen. Ook bij het onderbroken worden door een belletje, het openen van een andere app, wordt gechecked of er wordt opgenomen, indien dat zo is, wordt de opname afgesloten.






