### Inrichting Zaakafhandelcomponent 

De Zaakafhandelcomponent (ZAC) is een applicatie bedoeld voor het behandelen van zaken en managen van de werkvoorraad van zaken. De applicatie is daarmee ook gepositioneerd in de interactielaag van het 5 lagen model van Common Ground. 
Om zaken te registeren en behandelen maakt de applicatie gebruik van registratiecomponenten die zicht in de datalaag bevinden. Een groot deel van de inrichting zal dan ook in deze componenten gebeuren, een belangrijke daarvan is de zaaktypecatalogus. Om de ZAC in gebruik te nemen en te werken met de ingerichte zaaktypecatalogus is configuratie in de ZAC noodzakelijk. Daarnaast is er een aantal instellingen in de ZAC beschikbaar om het werken met het component naar eigen wens in te richten. Alle benodigde inrichting is in deze handleiding beschreven.

**Beheerinstellingen**
Het inrichten van de ZAC gaat via de beheermenu dat rechts in de werkbalk te openen is via het radar icoon. Het beheermenu bestaat 5 onderdelen, bij het openen van de beheerinstellingen is standaard ‘Inrichtingscheck’ geopend.

### Zaakafhandel-parameters  

De zaakafhandel-parameters (hierna ‘zaps’) zijn bedoeld om een zaaktype dat in de ZAC gebruikt wordt in te richten.

**Werking van de zaakafhandel-parameters**
Bij het openen van de zaps worden alle zaaktypen uit de zaaktypecatalogus getoond. Ook de oudere versies met een eindde geldigheid worden opgehaald. Vanuit het overzicht kan een zaaktype geopend worden om deze in te richten. Wijzigingen in een actief zaaktype zijn na het opslaan direct zichtbaar in de ZAC.

**Inrichten van een zaaktype**
Om de zaps te benaderen ga je door op het radar icoon te klikken naar de Beheer-instellingen. Open in het menu de ‘Zaakafhandel-parameters’. Alle zaaktypen worden opgehaald en het overzicht wordt geopend. Het is mogelijk om in dit overzicht te filteren en sorteren.
![image](https://user-images.githubusercontent.com/95233683/207916618-434d6cbc-d8f1-4522-aeec-7556d11b8e27.png)
!Klik op het bolletje links van het zaaktype kolom om snel te filteren op geldig en niet geldig!
Stappen:
1 Klik in het overzicht op het oog icoon van het zaaktype dat je wilt inrichten
2 In het tabblad ‘Algemene gegevens’ worden de volgende aspecten ingericht:
CMMN model (v)| het zaakafhandelmodel waarmee de zaak wordt afgehandeld
Groep (v)|  de groep die standaard bij zaaktoewijzing wordt ingevuld als een gebruiker de zaak aanmaakt. Als de zaak op een andere wijze wordt aangemaakt, bijvoorbeeld via een productaanvraag, dan is dit de groep waar een nieuwe zaak initieel op gezet wordt
Behandelaar |  de behandelaar waar een nieuwe zaak na het aanmaken initieel op gezet wordt
Streefdatum waarschuwingsvenster | het aantal kalenderdagen voordat de streefdatum van de zaak wordt bereikt dat bepaalt:
wanneer zaken een waarschuwingsindicatie (rode driehoek) krijgen
wanneer de signalering ‘Mijn zaak nadert de streefdatum’ wordt verstuurd
wanneer een zaak in de dashboardkaart ‘Mijn binnenkort verlopende zaken’ verschijnt
Fatale datum waarschuwingsvenster | het aantal kalenderdagen voordat de fatale datum van de zaak wordt bereikt dat bepaalt:
wanneer zaken een waarschuwingsindicatie (rode driehoek) krijgen
wanneer de signalering ‘Mijn zaak nadert de fatale datum’ wordt verstuurd
wanneer een zaak in de dashboardkaart ‘Mijn binnenkort verlopende zaken’ verschijnt
Statusmail intake fase (v) | bepaalt of bij het afronden van de fase ‘Intake’ de optie voor het versturen van een e-mail beschikbaar is en of deze standaard aangevinkt is
Statusmail afronden fase (v) | bepaalt of bij het afronden van de fase ‘In behandeling’ de optie voor het versturen van een e-mail beschikbaar is en of deze standaard aangevinkt is
Productaanvraagtype | het id van de productaanvraag zoals deze in Overige Registraties is ingericht. Deze instelling bepaalt dus voor een in Open Formulieren ingevuld formulier dat in Overige Registraties is geregistreerd van welk zaaktype door de ZAC een zaak aangemaakt moet worden.
3 Klik op de knop Volgende om naar het volgende tabblad ‘Taak gegevens’ te gaan. Hier worden alle beschikbare taken van het CMMN-model getoond. Standaard staan alle taken aan maar het is mogelijk om een taak via het schuifje uit te zetten waardoor deze tijdens de zaakbehandeling niet bechikbaar is.
 
Klik op een taak om de instellingen te openen. Iedere taak heeft standaard 3 instellingen:
Formulierdefinitie (v) | welk formulier voor het taakbehandelformulier wordt gebruikt
Groep | de groep die standaard bij taaktoewijzing wordt ingevuld als een gebruiker de taak start
Doorlooptijd | bepaalt de fatale datum van de taak
In sommige taakbehandelformulieren komen keuzelijsten voor waarvan de opties via een referentietabel aangepast kunnen worden. Welke referentietabel in dat taakbehandelformulier gebruikt wordt is dan te zien bij de instelling ‘Referentietabel voor ...’. Om de opties aan te passen kun je later naar de menukeuze ‘Referentietabellen’ gaan en daar de juiste tabel te kiezen, dit wordt elders in deze handleiding omschreven. Het is ook mogelijk om zelf een referentietabel aan te maken en deze in het taakbehandelformulier te gebruiken. Na het aanmaken van de tabel kan deze in de lijst bij de instelling ‘Referentietabel voor ...’ gekozen worden.
 
4 Ga verder naar het tabblad ‘Actie gegevens’. Bij de acties waarmee een gebruiker een fase afrondt is het mogelijk om een toelichting te tonen, denk aan een herinnering aan een belangrijke handeling die in die fase moete zijn uitgevoerd. Klik op een fase om de toelichting in het veld in te vullen.
5 Ga verder naar ‘Mailtemplate gegevens’. Tijdens de zaakbehandeling gebruikt de ZAC een aantal e-mails dat verstuurd wordt, voornamelijk aan de klant. Sommige van deze mails worden verplicht verstuurd en andere zijn optioneel. Iedere e-mail heeft een eigen template dat de inhoud van het  onderwerp en het bericht bepaalt. In dit overzicht stel je in welke e-mail welke mailtemplate gebruikt. Iedere e-mail heeft een standaard mailtemplate. Om deze te bekijken of te bewerken kun je later naar naar de menukeuze ‘Mailtemplates’ gaan. Het is ook mogelijk om zelf een mailtemplate aan te maken en deze in een van de e-mails te gebruiken. Na het aanmaken van de template kan deze in de lijst bij de instelling ‘mailtemplate’ gekozen worden. Open een e-mail door er op te klikken en stel bij iedere e-mail het gewenste template in.
6 Ga verder naar ‘Zaakbeëindig gegevens’.  In dit tabblad kan voor een aantal situaties waarin de zaak wordt beëindigd het resultaat dat de zaak krijgt bepaald worden. De mogelijke resultaten zijn ingesteld bij het zaaktype. Stel voor de volgende situaties het resultaat in:
Zaak is niet ontvankelijk (v) | bepaalt het resultaat wanneer een gebruiker bij de actie ‘Intake afronden’ deze optie kiest.
Verzoek is bij verkeerde organisatie ingediend | dit is een van de opties wanneer een gebruiker de [Zaak afbreken] actie gebruikt. Om de optie te activeren vink je deze aan en stel je het resultaat in dat de zaak krijgt wanneer deze optie gekozen wordt. 
Verzoek is door initiator ingetrokken | dit is een van de opties wanneer een gebruiker de [Zaak afbreken] actie gebruikt. Om de optie te activeren vink je deze aan en stel je het resultaat in dat de zaak krijgt wanneer deze optie gekozen wordt.
Zaak is een duplicaat | dit is een van de opties wanneer een gebruiker de [Zaak afbreken] actie gebruikt. Om de optie te activeren vink je deze aan en stel je het resultaat in dat de zaak krijgt wanneer deze optie gekozen wordt.
7 Klik op ‘Opslaan’ om de zaps voor het zaaktype te bewaren. Het zaaktype is hierna actief te gebruiken in de ZAC.

### Referentietabellen

Referentietabellen worden in de ZAC gebruikt om de keuzes in keuzelijsten te beheren. Een keuzelijst heeft een standaard referentietabel gekoppeld waarin de waarden bewerkt kunnen worden. Er kan een referentietabel toegevoegd worden om deze vervolgens via de zaakafhandelparameters te koppelen aan een zaaktype en zo te gebruiken. Hiermee is het mogelijk om voor een zaaktype een van de standaard afwijkende referentietabel te gebruiken.

**Referentietabel bewerken**
Een standaard of zelf toegevoegde referentietabel kan als volgt bewerkt worden.
Stappen:
1 In het Beheer-instellingen menu kies je ‘Referentietabellen’
2 Open de tabel door op het oog icoon te klikken
3 Om een waarde te bewerken klik je op het potlood naast de waarde waardoor het veld geactiveerd wordt. Pas de waarde in het veld aan en klik op het vink icoon om deze op te slaan. Als je wilt annuleren klik je op het kruis en blijft de oude waarde bewaard.
![image](https://user-images.githubusercontent.com/95233683/207917111-de87b280-34c7-4299-847e-d661998e42c2.png)
4 Om een waarde aan de tabel toe te voegen klik je op het + icoon. De nieuwe waarde verschijnt in de lijst met standaard de tekst ‘Nieuwe waarde’. Pas deze waarde aan zoals in de vorige stap omschreven.
 
5 Om een waarde uit een tabel te verwijderen klik je in de regel van de waarde op het prullenbak icoon. De waarde wordt direct verwijderd.
 
### Mailtemplates

Tijdens de zaakbehandeling gebruikt de ZAC een aantal e-mails dat verstuurd wordt, voornamelijk aan de klant. Sommige van deze mails worden verplicht verstuurd en andere zijn optioneel. Iedere e-mail heeft een eigen template dat de inhoud van het  onderwerp en het bericht bepaalt.
Er zijn drie categoriën mails die verstuurd kunnen worden tijdens de zaakbehandeling. Hieronder volgt een overzicht van de mails per categorie:
Taak
Ontvangstbevestiging | deze mail wordt aan de klant verstuurd nadat de actie ‘Ontvangstbevestiging’ is uitgevoerd
Taak aanvullende informatie | deze mail wordt aan de klant verstuurd nadat de taak is gestart
Taak advies | deze mail wordt aan de adviseur verstuurd nadat de taak is gestart
Zaak
Zaak ontvankelijk | deze mail wordt aan de klant verstuurd nadat de actie ‘Intake afronden’ is uitgevoerd en de gebruiker voor ontvankelijk = ‘Ja’ heeft gekozen en ‘Verzend mail’ heeft aangevinkt
Zaak niet ontvankelijk | deze mail wordt aan de klant verstuurd nadat de actie ‘Intake afronden’ is uitgevoerd en de gebruiker voor ontvankelijk = ‘Nee’ heeft gekozen en ‘Verzend mail’ heeft aangevinkt
Zaak afgehandeld | deze mail wordt aan de klant verstuurd nadat de actie ‘Zaak afhandelen’ is uitgevoerd en de gebruiker ‘Verzend mail’ heeft aangevinkt
Signalering
Signalering zaak op naam | deze mail wordt verstuurd aan de gebruiker op wiens naam de zaak wordt gezet
Signalering zaak document toegevoegd | deze mail wordt verstuurd aan de gebruiker op wiens naam de zaak de zaak staat waar het document aan toegevoegd is
Signalering zaak verlopend (streefdatum) | deze mail wordt verstuurd aan de gebruiker op wiens naam de zaak staat
Signalering zaak verlopend (fatale datum) | deze mail wordt verstuurd aan de gebruiker op wiens naam de zaak staat
Signalering taak op naam | deze mail wordt verstuurd aan de gebruiker op wiens naam de taak wordt gezet
Signalering taak verlopen | deze mail wordt verstuurd aan de gebruiker op wiens naam de taak staat

**Werking van de mailtemplates**
De mailtemplates kunnen vanuit menukeuze ‘Mailtemplates’ benaderd worden. Vanuit het overzicht kan een template ingezien en bewerkt worden. Het is ook mogelijk om zelf een template voor een  mail aan te maken.
Het gebruiken van de mailtemplates uit de categorie taak en zaak gebeurt door deze te koppelen via de zaakafhandelparameters, zie hoofdstuk Zaakafhandelparameters voor een beschrijving hiervan.
De mailtemplates voor de signaleringen zijn automatisch gekoppeld en kunnen direct gebruikt worden.
Mailtemplate bewerken
Nadat een template is geopend kan het onderwerp en het bericht bewerkt worden. Voor het bericht kan gebruik gemaakt worden van de editor waarmee het mogelijk is om opmaak toe te voegen of bijvoorbeeld een link of afbeelding aan het bericht toe te voegen. Voor zowel het onderwerp als het bericht kan gebruik gemaakt worden van variabelen. De variabelen bevatten gegevens over de zaak of taak die bij het verzenden van de e-mail geresolved worden. Afhankelijk van de categorie zijn alleen de relevante variabelen beschikbaar om te gebruiken. Variabelen die verplicht gevuld horen te zijn, zoals zaaktype, worden als variabele getoond wanneer deze niet gevuld zijn. Zo is het duidelijk dat er iets mis gaat. Variabelen die optioneel gevuld kunnen zijn worden leeg gelaten.
Het is ook mogelijk om de naam van de mailtemplate te wijzigen.
Stappen:
1 In het Beheer-instellingen menu kies je ‘Mailtemplates’
2 Open het template door op het oog icoon te klikken
3 Wijzig het onderwerp of het bericht. Gebruik eventueel variabelen door op het + icoon te klikken en ze te selecteren uit de lijst
 
4 Klik op ‘Opslaan’ om de wijziging door te voeren

Mailtemplate aanmaken
Er kan voor de zaak- en taakmailtemplates een template worden toegevoegd om deze vervolgens via de zaakafhandelparameters te koppelen aan een zaaktype en te gebruiken. Hiermee is het mogelijk om voor een zaaktype een van de standaard afwijkende mailtemplate te gebruiken.
Zodra een zelf gemaakte template aan een zaaktype is gekoppeld dan is het niet meer mogelijk om deze te verwijderen.
Stappen:
1 In het Beheer-instellingen menu kies je ‘Mailtemplates’
2 Maak een nieuwe template aan door op het plus icoon (Toevoegen) te klikken
3 Vul de naam van de mailtemplate in
4 Kies uit de lijst bij ‘Mail’ voor welke mail je het template wilt maken
5 Vul het onderwerp en het bericht in
6 Klik op ‘Opslaan’ om de mailtemplate toe te voegen. Het template is daarna beschikbaar in het overzicht.

### Inrichtingscheck

Dit onderdeel is bedoeld als hulpmiddel om de inrichting van een zaaktype in zowel de ZAC als de zaaktypecatalogus te controleren op minimaal benodigde inrichting.

**Zaaktypecatalogus synchronisatie**
Na het wijzigen van data in de zaaktypecatalogus in Open Zaak is het nodig om de gegevens te synchroniseren. Synchronisatie kan gestart worden door op de blauwe knop te klikken
De volgende gegevens worden gesynchroniseerd: Zaaktypen, Informatieobjecttypen, Besluittypen, Zaaktype-informatieobjecttypen, Resultaattypen, Statustypen en Roltypen. 

**Zaaktype inrichtingscheck**
Hier kan voor een zaaktype dat nog niet volledig en correct is ingericht worden gecheckt welke onderdelen nog inrichting nodig hebben. Als een zaaktype niet in deze lijst voorkomt dan is de mininmaal benodigde inrichting correct. Er wordt hier een validatie uitgevoerd op de ZAC zaakafhandel-parameters en de zaaktypecatalogus implementatie.
Om een zaaktype in deze lijst te controleren klik je op de regel. Daarna worden alle inrichtingsonderdelen die aandacht nodig hebben geopend en wordt per onderdeel vermeld wat er niet correct is ingericht.
Voor nu worden de volgende onderdelen gecheckt:
Zaakafhandelparameters | er wordt gecheckt of deze volledig zijn ingericht
Statustypen | er wordt gecheckt of voor de werking van de ZAC vereiste statustypen zijn toegevoegd aan het zaaktype. Dit zijn momenteel ‘Intake’, ‘In behandeling’, ‘Heropend’ en ‘Afgerond’ waarbij ‘Afgerond’ het hoogste volgnummer moet hebben zodat dit de eindstatus wordt.
Rollen | er wordt gecheckt of de voor de werking van de ZAC vereiste rollen zijn toegevoegd aan het zaaktype. Dit zijn momenteel ‘Initiator’ en ‘Behandelaar’ die nodig zijn om de functionaliteit voor het toevoegen van een initiator aan een zaak en het op naam van een behandelaar zetten van een zaak moeglijk te maken. Daarnaast wordt gecheckt of er minimaal één andere rol is toegevoegd die gebruikt wordt bij de functionaliteit voor het toevoegen van betrokkenen aan eem zaak.
Informatieobjecttype | er wordt voor de werking van de ZAC gecheckt of het zaaktype aan de vereiste informatieobjecttypen is gekoppeld. Dit is momenteel ‘e-mail’ dat gebruikt wordt voor het als document toevoegen van vanuit de ZAC verzonden e-mails.
Besluittype | er wordt gecheckt of aan het zaaktype een besluittype is gekoppeld. Dit gebeurt alleen als aan het zaaktype een resultaattype is toegevoegd dat als afleidingswijze de begin- of vervaldatum van een besluit heeft.

**Referentielijsten en selectielijst inrichtingscheck**
Hier kan gecheckt worden of de voor de ZAC benodigde inrichting in de referentielijsten en selectielijsten correct is. Er wordt hier een validatie uitgevoerd tussen de ZAC en de referentielijsten en selectielijsten implementatie.
Voor nu worden de volgende onderdelen gecheckt:
Communicatiekanaal | er wordt gecheckt of het kanaal ‘E-formulier’ is toegevoegd. Deze is alleen vereist als er via Open Formulieren productaanvragen worden aangemaakt waarvoor de ZAC zaken voor aan moet maken.

### Signaleringen

De ZAC heeft naast signaleringen voor gebruikers, die in de gebruikershandleiding worden beschreven, ook signaleringen voor groepen. Deze kunnen worden verstuurd wanneer een zaak niet op naam van een behandelaar maar alleen op naam van een groep staan.

**Werking van de signaleringen**
Als er een trigger voor een signalering die niet voor een gebruiker is bestemd komt dan wordt gekeken of de groepsignalering is ingeschakeld. Als dit het geval is dan wordt het ingestelde e-mailadres gebruikt om de signaleringsmail naar toe te sturen. Het e-mailadres van de groep is in te stellen in het beheerportaal.
Er is één signalering beschikbaar voor groepen, dat is ‘Er is een zaak op de groep gezet’ die verstuurd wordt als er een zaak nieuw aan een groep wordt toegewezen zonder dat er ook een behandelaar is gekozen.

**Groepsignalering inschakelen**
Stappen:
1 In het Beheer-instellingen menu kies je ‘Groepsignalering-instellingen’
2 Kies de groep uit de keuzelijst die je wilt instellen
3 Schakel een signalering per e-mail in door deze aan te vinken
 



