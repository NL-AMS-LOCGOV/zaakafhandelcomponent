package net.atos.zac.enkelvoudiginformatieobject

import future.keywords
import data.net.atos.zac.rollen
import data.net.atos.zac.alle_zaaktypen
import input.user
import input.enkelvoudig_informatieobject

enkelvoudig_informatieobject_acties := {
    "lezen": lezen,
    "verwijderen": verwijderen,
    "koppelen": koppelen,
    "downloaden": downloaden,
    "toevoegen_nieuwe_versie": toevoegen_nieuwe_versie,
    "bewerken": bewerken,
    "vergrendelen": vergrendelen,
    "ontgrendelen": ontgrendelen,
    "ondertekenen": ondertekenen
}

default lezen := false
lezen {
    zaaktype_allowed
}

default verwijderen := false
verwijderen {
    rollen.recordmanager.id in user.rollen
}

default koppelen := false
koppelen {
    zaaktype_allowed
    enkelvoudig_informatieobject.definitief == false
    enkelvoudig_informatieobject.vergrendeld == false
}
koppelen {
    rollen.recordmanager.id in user.rollen
}

default downloaden := false
downloaden {
    zaaktype_allowed
}

default toevoegen_nieuwe_versie := false
toevoegen_nieuwe_versie {
    enkelvoudig_informatieobject.zaak_open == true
    zaaktype_allowed
    enkelvoudig_informatieobject.definitief == false
    onvergrendeld_of_vergrendeld_door_user == true
}

default bewerken := false
bewerken {
    enkelvoudig_informatieobject.zaak_open == true
    zaaktype_allowed
    enkelvoudig_informatieobject.definitief == false
    onvergrendeld_of_vergrendeld_door_user == true
}
bewerken {
    rollen.recordmanager.id in user.rollen
}

default vergrendelen := false
vergrendelen {
    enkelvoudig_informatieobject.zaak_open == true
    zaaktype_allowed
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

default onvergrendeld_of_vergrendeld_door_user := false
onvergrendeld_of_vergrendeld_door_user {
    enkelvoudig_informatieobject.vergrendeld == false
}
onvergrendeld_of_vergrendeld_door_user {
    enkelvoudig_informatieobject.vergrendeld == true
    enkelvoudig_informatieobject.vergrendeld_door == user.id
}

default zaaktype_allowed := false
zaaktype_allowed {
    not enkelvoudig_informatieobject.zaaktype
}
zaaktype_allowed {
    some rol
    rollen[rol].id in user.rollen
    alle_zaaktypen == rollen[rol].zaaktypen
}
zaaktype_allowed {
    some rol
    rollen[rol].id in user.rollen
    enkelvoudig_informatieobject.zaaktype in rollen[rol].zaaktypen
}

default ondertekenen := false
ondertekenen {
    enkelvoudig_informatieobject.zaak_open == true
    zaaktype_allowed
    enkelvoudig_informatieobject.definitief == false
    onvergrendeld_of_vergrendeld_door_user == true
}


