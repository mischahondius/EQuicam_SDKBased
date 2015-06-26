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

**NAVIGATIE**
Het idee was om 2 tabjes onder de actionbar weer te geven, mogelijkheid tot swipen tussen de twee hoofdschermen. Dit is helaas niet gelukt. Ik had te veel moeite met het gebruik van fragments helaas. Ik heb toen gekozen om toch voor de oude vertrouwde Views te gaan, zodat ik zeker wist dat het zou werken en alles goed in elkaar zou zitten. Wel is er in het beginscherm (Live) een gaaf Hamburger menu toegevoegd om te kunnen navigeren tussen de drie schermen. Er is gekozen om dit menu niet als optie aan te bieden bij de views Clips en Camera. Dit om een bepaalde flow te creeren welke steeds terugkeert naar LIVE als uitgangsbasis. Live is het hart van de app. Als men een camera aan het aanpassen is, wordt deze verwacht ook daadwerkelijk hier mee bezig te zijn, en niet tijdens het invoeren te bedenken "hey! ik ga eens clips bekijken". Dit gedrag wordt niet aangemoedigd. Bij Camera wordt aangemoedigd om daadwerkelijk de camera aan te passen en deze te bevestigen. Mocht men zich bedeanken kan er worden teruggegeaan naar het Live venster door middel van de Native Back knop op je android device. Dit maakt het ritme van het programma sneller, natuurlijker en gemakkelijker. Het is niet logisch om een menu uit te moeten klappen om terug te bewegen naar de plek waar je een scherm daarvoor was. Dezelfde filosofie is toegepast op het Clips scherm: ook hier wordt gebruik gemaakt van Android-eigen navigatie. Het is overbodig om een menu hiervoor te gebruiken.

**API/SDK's/Android functies**
- De opname wordt gedaan op basis van een SDK van VideoExpertsGroup. De instabiele codec van een RTSP stream is zeer moeilijk handelbaar. Dat is voor mij niet realistisch om op korte termijn zelf te schrijven. Zover ik kan zien is dit de enige partij die hier een serieuze oplossing voor heeft geschreven. De SDK werkt perfect. Een kleine opmerking: ik ben begonnen met schrijven in de SDK code. Toen was ik op dreef. Toen ik later echter de packagename en structuur van het project wilde aanpassen ging Android App studio steigeren. Ik kreeg op geen enkele manier de packagename aangepast. Het staat nu wel goed in de Manifest en de package heeft ook de correcte naam bij installatie op de telefoon. Er blijven echter overal sporen van de oude package name (mediaPlayer.SDK.test). Ik had het graag anders gehad, maar ik kwam er echt niet uit. Toen heb ik besloten om zo verder te werken en mij te focussen op het behalen van mijn doel: de App volledig werkende en naar wens krijgen.

-Android MediaPlayer voor afspelen opgenomen MP4's. Hier is niet vanaf geweken. De native mediaPlayer van Android bleek perfect te werken en makkelijk aanpasbaar. Ik heb de vorige en volgende knoppen weggesloopt, om te voorkomen dat de gebruiker op die manier zapt tussen video's. Er moet doelbewust gebruik gemaakt worden van de app. Een video moet worden opgezocht op basis van de naam van het paard, de tijd en datum van opname en de thumbnail. Het is geen speeltje. Er is gekozen om de video automatisch af te sluiten wanneer de video klaar is met afspelen. Zo kan men direct een andere video selecteren om te bekijken. Wellicht dat dit nog aangepast dient te worden in de toekomst. Er valt ook wat voor te zeggen dat men de video misschien wil terugspoelen en nog eens bekijken, dan is het vervelend als het venster automatisch sluit. Hiervan ben ik mij bewust.

**Data**

- Er is gekozen om een harcoded Default camera in te stellen. Wanneer men een eerste keer de app opstart zal de camera worden opgeslagen naar sharedPrefs. Wanneer er eigen camera wordt ingesteld, zal deze, na tenminste éénmalig verbonden te zijn geweest, de default Camera Url overschrijven. Wanneer de app wordt afgesloten, of de telefoon opnieuw wordt opgestart, blijft de laatst gebruikte Camera Url in geheugen. In principe hoeft men dus slechts eenmalig de eigen camera in te voeren.
- Daarnaast slaat de SDK code via de PlayerInstellingen.java alle camerainstellingen op naar de sharedPreferences. Hier gaat het om technische instellingen van de player, zoals de buffertijd, aspect ratio, en hoe met framedrops om te gaan.
- Grootste struikelblok was hoe om te gaan met de Thumbnails van de clips. Er is tot op heden nog geen slimme manier bedacht om om te gaan met de grote rekentaak die het apparaat dient te doen op het moment van het openen van de clips. Bij het openen van Clips worden alle Thumbnails aangemaakt, op basis van de video's die zich in de map "EQuicam Clips" bevinden op het apparaat. Deze thumbnails worden weggeschreven naar de map "EQuicam Thumbnails". Vervolgens worden de thumbs opgeslagen naar het werkgeheugen door middel van een thumbnailCache. Er valt volgens mij niet te ontkomen aan het feit dat de Bitmapbestanden moeten worden opgehaald en ingeladen naar het werkgeheugen. Dit kost tijd. Als oplossing heb ik een maximaal aantal terug te kijken clips toegepast. Op dit moment staat het nummer op 10. Slechts de laatste 10 clips zullen dus worden ingeladen. Aangezien dit een variabele is dit boven aan de code wordt toegevoegd, is dit aantal gemakkelijk aan te passen naar wens.
- Daarnaast heb ik een dialoogvenstertje toegevoegd om aan de gebruiker te bevestigen dat er ook daadwerkelijk wordt gewerkt aan het laden van de clips. Dit om verwarring en irritatie te voorkomen. Het dialoogvenster verdwijnt pas op het moment dat alle clips daadwerkelijk zijn geladen. Hier wordt gebruik gemaakt van een aparte thread voor het venstertje. Op het moment dat dit niet op een andere thread gedaan werd, moest eerst het venster worden weergegeven en dan worden ingeladen of andersom. Tegelijk kon dan niet natuurlijk. Dit was voor mij een zeer lastige zaak. Uiteindelijk wacht het venster dus echt op signaal van de nieuwe activity dat alle clips klaar staan, alvorens de clipsview wordt geopend. Goede zaak lijkt mij. Op de één of andere manier kreeg ik de bewegende progress Ring maar niet werkende helaas. Vervelend.


**Functies**

//Is Playing/Is Recording checks

private boolean						is_record

private boolean 					playing

Het bleek inderdaad handig, noodzakelijk zelfs, om bij te houden of er werd opgenomen en of afgespeeld. Dit om te voorkomen dat een opname dubbel wordt ingestart. Ook kon hierdoor worden tegengegaan dat gebruikers van scherm wisselen tijdens opname, wat zeer ongewenst is. De app zou hierdoor ook crashen. Als er bijvoorbeeld tijdens opname naar "Clips" gegaan zou worden, zou ClipsActivity trachten een nieuwe clip aan te maken van de video die op dat moment nog "in de maak" is. Dit trekt de functie niet die de thumbnails maakt. Ook metadata kan nog niet worden uitgelezen. Heeft de App moeite mee. Deze cornercase is nu voorkomen. Ook bij het onderbroken worden door een belletje, het openen van een andere app, wordt gechecked of er wordt opgenomen, indien dat zo is, wordt de opname afgesloten. De namen van de bools zijn wel aangepast om consistentie in zowel taal als taalgebruik te bewerkstelligen. De bools heten dan ook "aanHetOpnemen" en "aanHetAfspelen".

Er zijn heel wat functies geschreven, voornamelijk Getters en Setters. Dit voornamelijk voor het ophalen van de CameraUrl en het aanpassen daarvan. Daarnaast Getters en Setters voor de eigenschappen (metadata) van de Clips Objecten. Ik heb gekozen om van Clips een object te maken, zoals het hoort bij Object-Oriented programmeren. De Camera heb ik als Activity laten staan, aangezien er op dit moment wordt uitgegeaan van één camera op een EQuicam locatie. Op het moment dat ik de functionaliteit uitbreidt naar de optie om te LIVE schaken tussen camera's, zal er een Camera Klasse worden aangemaakt, zodat er meerdere Camera Objecten kunnen voorkomen. 











