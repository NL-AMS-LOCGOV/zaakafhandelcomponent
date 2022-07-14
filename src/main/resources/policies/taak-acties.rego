package net.atos.zac.taak

import future.keywords
import data.net.atos.zac.rollen
import data.net.atos.zac.alle_zaaktypen
import input.user
import input.taak

taak_acties := {
    "lezen": lezen,
    "creeeren_document": creeeren_document,
    "toevoegen_document": toevoegen_document,
    "wijzigen_toekenning": wijzigen_toekenning,
    "wijzigen_overig": wijzigen_overig,
    "wijzigen_formulier": wijzigen_formulier
}

default lezen := false
lezen {
    zaaktype_allowed == true
}

default creeeren_document := false
creeeren_document {
    taak.afgerond == false
    zaaktype_allowed == true
}

default toevoegen_document := false
toevoegen_document {
    taak.afgerond == false
    zaaktype_allowed == true
}

default wijzigen_toekenning := false
wijzigen_toekenning {
    taak.afgerond == false
    zaaktype_allowed == true
}

default wijzigen_overig := false
wijzigen_overig {
    taak.afgerond == false
    zaaktype_allowed == true
}

default wijzigen_formulier := false
wijzigen_formulier {
    taak.afgerond == false
    zaaktype_allowed == true
}

default zaaktype_allowed := false
zaaktype_allowed {
    some rol
    rollen[rol].id in user.rollen
    alle_zaaktypen == rollen[rol].zaaktypen
}
zaaktype_allowed {
    some rol
    rollen[rol].id in user.rollen
    taak.zaaktype in rollen[rol].zaaktypen
}
