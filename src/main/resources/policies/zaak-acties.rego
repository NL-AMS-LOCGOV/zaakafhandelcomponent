package net.atos.zac.zaak

import future.keywords
import data.net.atos.zac.rol.behandelaar
import data.net.atos.zac.rol.coordinator
import data.net.atos.zac.rol.recordmanager
import data.net.atos.zac.domein.domeinen
import data.net.atos.zac.domein.domein_elk_zaaktype
import input.zaak
import input.user

zaak_acties := {
    "lezen": lezen,
    "wijzigen": wijzigen,
    "wijzigen_toekenning": wijzigen_toekenning,
    "opschorten": opschorten,
    "verlengen": verlengen,
    "hervatten": hervatten,
    "afbreken": afbreken,
    "voortzetten": voortzetten,
    "heropenen": heropenen,
    "creeeren_document": creeeren_document,
    "toevoegen_document": toevoegen_document,
    "koppelen": koppelen,
    "versturen_email": versturen_email,
    "versturen_ontvangstbevestiging": versturen_ontvangstbevestiging,
    "toevoegen_initiator_persoon": toevoegen_initiator_persoon,
    "toevoegen_initiator_bedrijf": toevoegen_initiator_bedrijf,
    "verwijderen_initiator": verwijderen_initiator,
    "toevoegen_betrokkene_persoon": toevoegen_betrokkene_persoon,
    "toevoegen_betrokkene_bedrijf": toevoegen_betrokkene_bedrijf,
    "verwijderen_betrokkene": verwijderen_betrokkene,
    "toevoegen_bag_object": toevoegen_bag_object,
    "aanmaken_taak": aanmaken_taak,
    "vastleggen_besluit" : vastleggen_besluit
}

default zaaktype_allowed := false
zaaktype_allowed {
    domein_elk_zaaktype.rol in user.rollen
}
zaaktype_allowed {
    some domein
    domeinen[domein].rol in user.rollen
    zaak.zaaktype in domeinen[domein].zaaktypen
}

default lezen := false
lezen {
    { behandelaar, coordinator, recordmanager }[_].rol in user.rollen
    zaaktype_allowed == true
}

default wijzigen := false
wijzigen {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
}
wijzigen {
    recordmanager.rol in user.rollen
    zaaktype_allowed == true
}

default wijzigen_toekenning := false
wijzigen_toekenning {
    { behandelaar, coordinator, recordmanager }[_].rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
}

default verlengen := false
verlengen {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
    zaak.heropend == false
}

default opschorten := false
opschorten {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
    zaak.heropend == false
    zaak.opgeschort == false
}

default hervatten := false
hervatten {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
    zaak.heropend == false
    zaak.opgeschort == true
}

default afbreken := false
afbreken {
    { behandelaar, recordmanager }[_].rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
    zaak.heropend == false
}

default voortzetten := false
voortzetten {
    { behandelaar, recordmanager }[_].rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
}

default heropenen := false
heropenen {
    recordmanager.rol in user.rollen
    zaaktype_allowed == true
    zaak.open == false
}

default creeeren_document := false
creeeren_document {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
}
creeeren_document {
    recordmanager.rol in user.rollen
    zaaktype_allowed == true
}

default toevoegen_document := false
toevoegen_document {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
}
toevoegen_document {
    recordmanager.rol in user.rollen
    zaaktype_allowed == true
}

default koppelen := false
koppelen {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
}
koppelen {
    recordmanager.rol in user.rollen
    zaaktype_allowed == true
}

default versturen_email := false
versturen_email {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
}

default versturen_ontvangstbevestiging := false
versturen_ontvangstbevestiging {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
    zaak.heropend == false
    zaak.ontvangstbevestigingVerstuurd == false
}

default toevoegen_initiator_persoon := false
toevoegen_initiator_persoon {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
}

default toevoegen_initiator_bedrijf := false
toevoegen_initiator_bedrijf {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
}

default verwijderen_initiator := false
verwijderen_initiator {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
}

default toevoegen_betrokkene_persoon := false
toevoegen_betrokkene_persoon {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
}

default toevoegen_betrokkene_bedrijf := false
toevoegen_betrokkene_bedrijf {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
}

default verwijderen_betrokkene := false
verwijderen_betrokkene {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
}

default toevoegen_bag_object := false
toevoegen_bag_object {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
}

default aanmaken_taak := false
aanmaken_taak {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
}

default vastleggen_besluit := false
vastleggen_besluit {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
    zaak.open == true
    zaak.besluit == false
    zaak.heeft_besluittypen == true
}
