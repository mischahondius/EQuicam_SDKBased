# EQuicam App
Mischa Hondius, 6053017.

**Kort:** live bekijken van videobeelden + opnemen en terugkijken van deze beelden.

De EQuicam App maakt het mogelijk om live videobeelden van EQuicam (ip camera) te bekijken en op te nemen op je Android apparaat. De EQuicam is de eerste videocamera die volledig automatisch paard en ruiter in beeld brengt. De camera zoomt, pant en tilt zelf mee op basis van beeldherkenning. Hierbij is dus geen "tracking device" nodig. De combinatie EQuicam + app biedt ruiters en trainers binnen de hippische sport de mogelijkheid tot het gemakkelijk opnemen en analyseren van trainingen, zonder dat daarbij een dure cameraman aan ten pas hoeft  te komen.

**Functionaliteit Minimale App**
- Verbinden met EQuicam
- Live weergeven beeld camera
- Opname live beeld
- Terugkijken opgenomen clips

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

**1. LIVE**
Op dit scherm kan de gebruiker het live beeld van de camera bekijken. Om ongewenst dataverbruik te voorkomen dient te gebruiker doelbewust een verbinding te maken met de camera. Op dit moment is daar een aparte knop voor. Doel is om dit te veranderen in een tik op het lege videoscherm.
Deze zal dan de tekst "Tik op het scherm om verbinding te maken met uw EQuicam" weergeven. Een seperate knop lijkt mij hiervoor overbodig.

<img src="/docs/LIVE_1.png" width="400">
<img src="/docs/LIVE_2.png" width="400">

Tijdens het verbinden krijgt de gebruiker feedback in de vorm van de tekst "Verbinden...", wat bevestigt aan gebruiker dat er daadwerkelijk iets gebeurt.

Na dat er een verbinding is gelegd wordt in het videovenster live beeld weergegeven..

<img src="/docs/LIVE_3.png" width="400">
<img src="/docs/LIVE_4.png" width="400">

Door middel van de grote rode opvallende opnameknop kan er een opname worden gestart. Opname wordt bevestigt op meerdere manieren:
- 1. Toast message
- 2. Gloeiende opnameknop
- 3. Opname duratie, er gaat een timer lopen (Nog in te voegen)
- 4. Optioneel: rood lampje of REC rechtsboven in beeld videovenster

<img src="/docs/LIVE_5.png" width="400">

Stop opname wordt door het ongedaanmaken van bovenstaande bevestigt. De Toast-message "Opname Gestopt".

<img src="/docs/CLIPS_1.png" width="400">

**2. CLIPS**
Een scherm waarbij een lijst van video's wordt weergegeven die met deze App zijn opgenomen. Een lijst met video inclusief thumbnails wordt weergegeven. Bovenin beeld blijft de videoplayer zichtbaar. Indien er een video wordt aangeklikt wordt deze direct geopend in de player. Doel is om hier ook Fullscreen mogelijk te maken. Ook door middel van een orientatie beweging naar landscape. 

NAVIGATIE
- Doel is om 2 tabjes onder de actionbar weer te geven, mogelijkheid tot swipen tussen de twee hoofdschermen.

**API/SDK's/Android functies**
- De opname wordt gedaan op basis van een SDK van VideoExpertsGroup. De instabiele codec van een RTSP stream is zeer moeilijk handelbaar. Dat is voor mij niet realistisch om op korte termijn zelf te schrijven. Zover ik kan zien is dit de enige partij die hier een serieuze oplossing voor heeft geschreven. 
-ActionBar tabs
-Android MediaPlayer voor afspelen opgenomen MP4's. (Evt. Alsnog SDK videoplayer ivm evt instabiliteit videostreams).

**Data**
- Hardcoded RTSP Stream url voor verbinding met Camera (kan later aanpasbaar worden en bijv. worden opgeslagen in SharedPrefs of gekoppeld aan een user account opgeslagen in een online database)
- Evt. opslag metadata bij video's -> in SharedPrefs, koppeling op basis bestandsnaam?

**Functies**

//Is Playing/Is Recording checks

private boolean						is_record

private boolean 					playing






