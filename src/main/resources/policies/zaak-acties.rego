package net.atos.zac

import future.keywords

zaak_acties := {
    "opschorten": opschorten,
    "verlengen": verlengen,
    "hervatten": hervatten,
    "afbreken": afbreken,
    "afsluiten": afsluiten,
    "heropenen": heropenen,
    "creeeren_document": creeeren_document,
    "toevoegen_document": toevoegen_document,
    "ontkoppelen_document": ontkoppelen_document,
    "koppelen_zaak": koppelen_zaak,
    "koppelen_aan_zaak": koppelen_aan_zaak,
    "versturen_email": versturen_email,
    "versturen_ontvangstbevestiging": versturen_ontvangstbevestiging,
    "toevoegen_persoon": toevoegen_persoon,
    "toevoegen_bedrijf": toevoegen_bedrijf,
    "verwijderen_initiator": verwijderen_initiator,
    "wijzigen_toekenning": wijzigen_toekenning,
    "wijzigen_data": wijzigen_data,
    "wijzigen_overig": wijzigen_overig,
    "starten_plan_items": starten_plan_items
    }

default heropenen := false
heropenen {
    input.zaak.open == false
}

default opschorten := false
opschorten {
    input.zaak.open == true
    input.zaak.heropend == false
    input.zaak.opgeschort == false
}

default hervatten := false
hervatten {
    input.zaak.open == true
    input.zaak.heropend == false
    input.zaak.opgeschort == true
}

default verlengen := false
verlengen {
    input.zaak.open == true
    input.zaak.heropend == false
}

default afbreken := false
afbreken {
    input.zaak.open == true
    input.zaak.openDeelzaken == false
    input.zaak.heropend == false
}

default afsluiten := false
afsluiten {
    input.zaak.open == true
    input.zaak.openDeelzaken == false
}

default creeeren_document := false
creeeren_document {
    input.zaak.open == true
}

default toevoegen_document := false
toevoegen_document {
    input.zaak.open == true
}

default ontkoppelen_document := false
ontkoppelen_document {
    input.zaak.open == true
}

default koppelen_zaak := true

default koppelen_aan_zaak := true

default versturen_email := false
versturen_email {
    input.zaak.open == true
}

default versturen_ontvangstbevestiging := false
versturen_ontvangstbevestiging {
    input.zaak.open == true
}

default toevoegen_persoon := false
toevoegen_persoon {
    input.zaak.open == true
}

default toevoegen_bedrijf := false
toevoegen_bedrijf {
    input.zaak.open == true
}

default verwijderen_initiator := false
verwijderen_initiator {
    input.zaak.open == true
}

default wijzigen_toekenning := false
wijzigen_toekenning {
    input.zaak.open == true
}

default wijzigen_data := false
wijzigen_data {
    input.zaak.open == true
}

default starten_plan_items := false
starten_plan_items {
    input.zaak.open == true
    input.zaak.behandelaar == input.user.id
}

default wijzigen_overig := false
wijzigen_overig {
    input.zaak.open == true
}
