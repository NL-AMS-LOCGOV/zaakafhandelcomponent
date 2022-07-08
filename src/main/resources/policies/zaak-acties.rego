package net.atos.zac.zaak

import future.keywords
import input.zaak
import input.user

zaak_acties := {
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

default heropenen := false
heropenen {
    zaak.open == false
}

default opschorten := false
opschorten {
    zaak.open == true
    zaak.heropend == false
    zaak.opgeschort == false
}

default hervatten := false
hervatten {
    zaak.open == true
    zaak.heropend == false
    zaak.opgeschort == true
}

default verlengen := false
verlengen {
    zaak.open == true
    zaak.heropend == false
}

default afbreken := false
afbreken {
    zaak.open == true
    zaak.heropend == false
}

default afsluiten := false
afsluiten {
    zaak.open == true
}

default creeeren_document := false
creeeren_document {
    zaak.open == true
}

default toevoegen_document := false
toevoegen_document {
    zaak.open == true
}

default koppelen := true

default versturen_email := false
versturen_email {
    zaak.open == true
}

default versturen_ontvangstbevestiging := false
versturen_ontvangstbevestiging {
    zaak.open == true
}

default toevoegen_persoon := false
toevoegen_persoon {
    zaak.open == true
}

default toevoegen_bedrijf := false
toevoegen_bedrijf {
    zaak.open == true
}

default verwijderen_initiator := false
verwijderen_initiator {
    zaak.open == true
}

default wijzigen_toekenning := false
wijzigen_toekenning {
    zaak.open == true
}

default starten_plan_items := false
starten_plan_items {
    zaak.open == true
    zaak.behandelaar == user.id
}

default wijzigen_overig := false
wijzigen_overig {
    zaak.open == true
}
