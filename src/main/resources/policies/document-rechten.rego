package net.atos.zac.document

import future.keywords
import data.net.atos.zac.rol.behandelaar
import data.net.atos.zac.rol.coordinator
import data.net.atos.zac.rol.recordmanager
import data.net.atos.zac.domein.domeinen
import data.net.atos.zac.domein.domein_elk_zaaktype
import input.user
import input.document

document_rechten := {
    "lezen": lezen,
    "wijzigen": wijzigen,
    "verwijderen": verwijderen,
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

default verwijderen := false
verwijderen {
    recordmanager.rol in user.rollen
    zaaktype_allowed
}

default vergrendelen := false
vergrendelen {
    behandelaar.rol in user.rollen
    zaaktype_allowed
    document.zaak_open == true
    document.definitief == false
}
vergrendelen {
    recordmanager.rol in user.rollen
    zaaktype_allowed
}

default ontgrendelen := false
ontgrendelen {
    behandelaar.rol in user.rollen
    zaaktype_allowed
    document.vergrendeld_door == user.id
}
ontgrendelen {
    recordmanager.rol in user.rollen
    zaaktype_allowed
}

default ondertekenen := false
ondertekenen {
    behandelaar.rol in user.rollen
    zaaktype_allowed
    document.zaak_open == true
    onvergrendeld_of_vergrendeld_door_user == true
}


