package net.atos.zac.enkelvoudiginformatieobject

import future.keywords
import data.net.atos.zac.rol.recordmanager
import data.net.atos.zac.domein.domeinen
import data.net.atos.zac.domein.domein_elk_zaaktype
import input.user
import input.enkelvoudig_informatieobject

enkelvoudig_informatieobject_acties := {
    "lezen": lezen,
    "bewerken": bewerken,
    "toevoegen_nieuwe_versie": toevoegen_nieuwe_versie,
    "koppelen": koppelen,
    "verwijderen": verwijderen,
    "downloaden": downloaden,
    "vergrendelen": vergrendelen,
    "ontgrendelen": ontgrendelen,
    "ondertekenen": ondertekenen
}

default zaaktype_allowed := false
zaaktype_allowed {
    not enkelvoudig_informatieobject.zaaktype
}
zaaktype_allowed {
    domein_elk_zaaktype.rol in user.rollen
}
zaaktype_allowed {
    some domein
    domeinen[domein].rol in user.rollen
    enkelvoudig_informatieobject.zaaktype in domeinen[domein].zaaktypen
}

default onvergrendeld_of_vergrendeld_door_user := false
onvergrendeld_of_vergrendeld_door_user {
    enkelvoudig_informatieobject.vergrendeld == false
}
onvergrendeld_of_vergrendeld_door_user {
    enkelvoudig_informatieobject.vergrendeld == true
    enkelvoudig_informatieobject.vergrendeld_door == user.id
}

default lezen := false
lezen {
    { behandelaar, coordinator, recordmanager }[_].rol in user.rollen
    zaaktype_allowed
}

default bewerken := false
bewerken {
    behandelaar.rol in user.rollen
    zaaktype_allowed
    enkelvoudig_informatieobject.zaak_open == true
    enkelvoudig_informatieobject.definitief == false
    onvergrendeld_of_vergrendeld_door_user == true
}
bewerken {
    recordmanager.rol in user.rollen
    zaaktype_allowed
}

default toevoegen_nieuwe_versie := false
toevoegen_nieuwe_versie {
    behandelaar.rol in user.rollen
    zaaktype_allowed
    enkelvoudig_informatieobject.zaak_open == true
    enkelvoudig_informatieobject.definitief == false
    onvergrendeld_of_vergrendeld_door_user == true
}
toevoegen_nieuwe_versie {
    recordmanager.rol in user.rollen
    zaaktype_allowed
}

default koppelen := false
koppelen {
    behandelaar.rol in user.rollen
    zaaktype_allowed
    enkelvoudig_informatieobject.definitief == false
    enkelvoudig_informatieobject.vergrendeld == false
}
koppelen {
    recordmanager.rol in user.rollen
    zaaktype_allowed
}

default verwijderen := false
verwijderen {
    recordmanager.rol in user.rollen
    zaaktype_allowed
}

default downloaden := false
downloaden {
    { behandelaar, coordinator, recordmanager }[_].rol in user.rollen
    zaaktype_allowed
}

default vergrendelen := false
vergrendelen {
    behandelaar.rol in user.rollen
    zaaktype_allowed
    enkelvoudig_informatieobject.zaak_open == true
    enkelvoudig_informatieobject.definitief == false
    enkelvoudig_informatieobject.vergrendeld == false
}
vergrendelen {
    recordmanager.rol in user.rollen
    zaaktype_allowed
    enkelvoudig_informatieobject.vergrendeld == false
}

default ontgrendelen := false
ontgrendelen {
    behandelaar.rol in user.rollen
    zaaktype_allowed
    enkelvoudig_informatieobject.vergrendeld == true
    enkelvoudig_informatieobject.vergrendeld_door == user.id
}
ontgrendelen {
    recordmanager.rol in user.rollen
    zaaktype_allowed
    enkelvoudig_informatieobject.vergrendeld == true
}

default ondertekenen := false
ondertekenen {
    behandelaar.rol in user.rollen
    zaaktype_allowed
    enkelvoudig_informatieobject.zaak_open == true
    enkelvoudig_informatieobject.ondertekend == false
    onvergrendeld_of_vergrendeld_door_user == true
}


