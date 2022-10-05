package net.atos.zac.taak

import future.keywords
import data.net.atos.zac.rol.behandelaar
import data.net.atos.zac.rol.recordmanager
import data.net.atos.zac.domein.domeinen
import data.net.atos.zac.domein.domein_elk_zaaktype
import input.user
import input.taak

taak_rechten := {
    "lezen": lezen,
    "wijzigen": wijzigen,
    "toekennen": toekennen,
    "wijzigen_formulier": wijzigen_formulier,
    "creeeren_document": creeeren_document,
    "toevoegen_document": toevoegen_document
}

default zaaktype_allowed := false
zaaktype_allowed {
    domein_elk_zaaktype.rol in user.rollen
}
zaaktype_allowed {
    some domein
    domeinen[domein].rol in user.rollen
    taak.zaaktype in domeinen[domein].zaaktypen
}

default lezen := false
lezen {
    { behandelaar, recordmanager }[_].rol in user.rollen
    zaaktype_allowed == true
}

default wijzigen := false
wijzigen {
    { behandelaar, recordmanager }[_].rol in user.rollen
    zaaktype_allowed == true
}

default toekennen := false
toekennen {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
}

default wijzigen_formulier := false
wijzigen_formulier {
    { behandelaar, recordmanager }[_].rol in user.rollen
    zaaktype_allowed == true
}

default creeeren_document := false
creeeren_document {
    { behandelaar, recordmanager }[_].rol in user.rollen
    zaaktype_allowed == true
}

default toevoegen_document := false
toevoegen_document {
    { behandelaar, recordmanager }[_].rol in user.rollen
    zaaktype_allowed == true
}



