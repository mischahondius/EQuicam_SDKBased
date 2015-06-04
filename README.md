# EQuicam App
Mischa Hondius, 6053017.

**Kort:** live bekijken van videobeelden + opnemen en terugkijken van deze beelden.

De EQuicam App maakt het mogelijk om live videobeelden van EQuicam (ip camera) te bekijken en op te nemen op je Android apparaat. De EQuicam is de eerste videocamera die volledig automatisch paard en ruiter in beeld brengt. De camera zoomt, pant en tilt zelf mee op basis van beeldherkenning. Hierbij is dus geen "tracking device" nodig. De combinatie EQuicam + app biedt ruiters en trainers binnen de hippische sport de mogelijkheid tot het gemakkelijk opnemen en analyseren van trainingen, zonder dat daarbij een dure cameraman aan ten pas hoeft  te komen. 

**Features:**
- Bekijk live videobeelden
- Neem clips lokaal op 
- Terugkijken van clips

_Optioneel_
- Laatst opgenomen item op hoofscherm
- Zoekfunctie opgenomen clips
- Aanpassen metadata clips
- Zelf meerdere camera's toevoegen/verwijderen/bekijken
- Opnemen van commentaar trainer live of achteraf, externe mic of intern telefoon
- Accounts, met eigen camera's daaronder
- Favorieten (clips)

**Schetsen**
![alt text](https://github.com/mischahondius/equicam_android/blob/master/docs/schets.jpg "Schets")

**Data/bronnen**
- Live beeld van camera (RTSP stream, via DynamischDNS, dus vast adres)

**Samenwerking tussen onderdelen**
- Livebeeld opnemen, clip moet worden weggeschreven op interne opslag
- Listview van opgenomen clips moet de inhoud van een bepaalde map ophalen en weergeven
- Bij aanklikken van één clip, meegeven gekozen clip aan "watch"-layout, watch layout geeft clip weer

**Platform**
- Android heeft een MediaPlayer & MediaRecorder object voor weergave en opname van video
- Mogelijk gebruik van Video Experts Group SDK

**Potentiële problemen**
- Codec problemen/instabiele beeldstream (lagere bitrate/resolutie proberen, andere codec proberen)
- Toestemmingen nodig voor video's opslaan op apparaat?
- Opname tot mobiel vol is en crashed, moet beperkt worden
 
**Vergelijkbare Apps**
- Youtube App
- VLC App
- GoPro App

Technisch moet het allemaal prima mogelijk zijn.

