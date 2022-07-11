package net.atos.zac.enkelvoudiginformatieobject

import future.keywords
import input.enkelvoudig_informatieobject
import input.zaak
import input.user
import data.net.atos.zac.rollen

enkelvoudig_informatieobject_acties := {
    "verwijderen": verwijderen,
    "koppelen": koppelen,
    "downloaden": downloaden,
    "toevoegen_nieuwe_versie": toevoegen_nieuwe_versie,
    "bewerken": bewerken,
    "vergrendelen": vergrendelen,
    "ontgrendelen": ontgrendelen
}

default verwijderen := false
verwijderen {
    rollen.recordmanager.id in user.rollen
}

default koppelen := false
koppelen {
    zaak.open == true
    enkelvoudig_informatieobject.definitief == false
    enkelvoudig_informatieobject.vergrendeld == false
}
koppelen {
    rollen.recordmanager.id in user.rollen
}

default downloaden := true

default toevoegen_nieuwe_versie := false
toevoegen_nieuwe_versie {
    zaak.open == true
    enkelvoudig_informatieobject.definitief == false
    onvergendeld_of_vergrendeld_door_user == true
}

default bewerken := false
bewerken {
    zaak.open == true
    enkelvoudig_informatieobject.definitief == false
    onvergendeld_of_vergrendeld_door_user == true
}

default vergrendelen := false
vergrendelen {
    enkelvoudig_informatieobject.vergrendeld == false
    enkelvoudig_informatieobject.definitief == false
}

default ontgrendelen := false
ontgrendelen {
    enkelvoudig_informatieobject.vergrendeld == true
    enkelvoudig_informatieobject.vergrendeld_door == user.id
}
ontgrendelen {
    enkelvoudig_informatieobject.vergrendeld == true
    rollen.recordmanager.id in user.rollen
}

default onvergendeld_of_vergrendeld_door_user := false
onvergendeld_of_vergrendeld_door_user {
    enkelvoudig_informatieobject.vergrendeld == false
}
onvergendeld_of_vergrendeld_door_user {
    enkelvoudig_informatieobject.vergrendeld == true
    enkelvoudig_informatieobject.vergrendeld_door == user.id
}
