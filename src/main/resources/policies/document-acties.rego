package net.atos.zac.document

import future.keywords
import data.net.atos.zac.rol.behandelaar
import data.net.atos.zac.rol.coordinator
import data.net.atos.zac.rol.recordmanager
import data.net.atos.zac.domein.domeinen
import data.net.atos.zac.domein.domein_elk_zaaktype
import input.user
import input.document

document_acties := {
    "lezen": lezen,
    "wijzigen": wijzigen,
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
    not document.zaaktype
}
zaaktype_allowed {
    domein_elk_zaaktype.rol in user.rollen
}
zaaktype_allowed {
    some domein
    domeinen[domein].rol in user.rollen
    document.zaaktype in domeinen[domein].zaaktypen
}

default onvergrendeld_of_vergrendeld_door_user := false
onvergrendeld_of_vergrendeld_door_user {
    document.vergrendeld == false
}
onvergrendeld_of_vergrendeld_door_user {
    document.vergrendeld == true
    document.vergrendeld_door == user.id
}

default lezen := false
lezen {
    { behandelaar, coordinator, recordmanager }[_].rol in user.rollen
    zaaktype_allowed
}

default wijzigen := false
wijzigen {
    behandelaar.rol in user.rollen
    zaaktype_allowed
    document.zaak_open == true
    document.definitief == false
    onvergrendeld_of_vergrendeld_door_user == true
}
wijzigen {
    recordmanager.rol in user.rollen
    zaaktype_allowed
}

default toevoegen_nieuwe_versie := false
toevoegen_nieuwe_versie {
    behandelaar.rol in user.rollen
    zaaktype_allowed
    document.zaak_open == true
    document.definitief == false
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
    document.definitief == false
    document.vergrendeld == false
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
    document.zaak_open == true
    document.definitief == false
    document.vergrendeld == false
}
vergrendelen {
    recordmanager.rol in user.rollen
    zaaktype_allowed
    document.vergrendeld == false
}

default ontgrendelen := false
ontgrendelen {
    behandelaar.rol in user.rollen
    zaaktype_allowed
    document.vergrendeld == true
    document.vergrendeld_door == user.id
}
ontgrendelen {
    recordmanager.rol in user.rollen
    zaaktype_allowed
    document.vergrendeld == true
}

default ondertekenen := false
ondertekenen {
    behandelaar.rol in user.rollen
    zaaktype_allowed
    document.zaak_open == true
    document.ondertekend == false
    onvergrendeld_of_vergrendeld_door_user == true
}


