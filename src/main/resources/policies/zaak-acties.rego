package net.atos.zac.zaak

import future.keywords
import data.net.atos.zac.rollen
import data.net.atos.zac.alle_zaaktypen
import input.zaak
import input.user

zaak_acties := {
    "lezen": lezen,
    "opschorten": opschorten,
    "verlengen": verlengen,
    "hervatten": hervatten,
    "afbreken": afbreken,
    "afsluiten": afsluiten,
    "heropenen": heropenen,
    "creeeren_document": creeeren_document,
    "toevoegen_document": toevoegen_document,
    "koppelen": koppelen,
    "versturen_email": versturen_email,
    "versturen_ontvangstbevestiging": versturen_ontvangstbevestiging,
    "toevoegen_persoon": toevoegen_persoon,
    "toevoegen_bedrijf": toevoegen_bedrijf,
    "verwijderen_initiator": verwijderen_initiator,
    "wijzigen_toekenning": wijzigen_toekenning,
    "wijzigen_overig": wijzigen_overig,
    "starten_plan_items": starten_plan_items
}

default lezen := false
lezen {
    zaaktype_allowed == true
}

default heropenen := false
heropenen {
    zaak.open == false
    zaaktype_allowed == true
}

default opschorten := false
opschorten {
    zaak.open == true
    zaak.heropend == false
    zaak.opgeschort == false
    zaaktype_allowed == true
}

default hervatten := false
hervatten {
    zaak.open == true
    zaak.heropend == false
    zaak.opgeschort == true
    zaaktype_allowed == true
}

default verlengen := false
verlengen {
    zaak.open == true
    zaak.heropend == false
    zaaktype_allowed == true
}

default afbreken := false
afbreken {
    zaak.open == true
    zaak.heropend == false
    zaaktype_allowed == true
}

default afsluiten := false
afsluiten {
    zaak.open == true
    zaaktype_allowed == true
}

default creeeren_document := false
creeeren_document {
    zaak.open == true
    zaaktype_allowed == true
}

default toevoegen_document := false
toevoegen_document {
    zaak.open == true
    zaaktype_allowed == true
}

default koppelen := false
koppelen {
    zaak.open == true
    zaaktype_allowed == true
}
koppelen {
    rollen.recordmanager.id in user.rollen
    zaaktype_allowed == true
}

default versturen_email := false
versturen_email {
    zaak.open == true
    zaaktype_allowed == true
}

default versturen_ontvangstbevestiging := false
versturen_ontvangstbevestiging {
    zaak.open == true
    zaaktype_allowed == true
}

default toevoegen_persoon := false
toevoegen_persoon {
    zaak.open == true
    zaaktype_allowed == true
}

default toevoegen_bedrijf := false
toevoegen_bedrijf {
    zaak.open == true
    zaaktype_allowed == true
}

default verwijderen_initiator := false
verwijderen_initiator {
    zaak.open == true
    zaaktype_allowed == true
}

default wijzigen_toekenning := false
wijzigen_toekenning {
    zaak.open == true
    zaaktype_allowed == true
}

default starten_plan_items := false
starten_plan_items {
    zaak.open == true
    zaak.behandelaar == user.id
    zaaktype_allowed == true
}

default wijzigen_overig := false
wijzigen_overig {
    zaak.open == true
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
    zaak.zaaktype in rollen[rol].zaaktypen
}
