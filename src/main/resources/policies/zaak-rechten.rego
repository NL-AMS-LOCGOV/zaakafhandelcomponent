package net.atos.zac.zaak

import future.keywords
import data.net.atos.zac.rol.behandelaar
import data.net.atos.zac.rol.coordinator
import data.net.atos.zac.rol.recordmanager
import input.zaak
import input.user

zaak_rechten := {
    "lezen": lezen,
    "wijzigen": wijzigen,
    "toekennen": toekennen,
    "behandelen": behandelen,
    "afbreken": afbreken,
    "heropenen": heropenen,
    "wijzigenZaakdata": wijzigenZaakdata,
    "wijzigenDoorlooptijd": wijzigenDoorlooptijd
}

default zaaktype_allowed := false
zaaktype_allowed {
    not user.zaaktypen
}
zaaktype_allowed {
    zaak.zaaktype in user.zaaktypen
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

default toekennen := false
toekennen {
    { behandelaar, coordinator, recordmanager }[_].rol in user.rollen
    zaaktype_allowed == true
}

default behandelen := false
behandelen {
    behandelaar.rol in user.rollen
    zaaktype_allowed == true
}

default afbreken := false
afbreken {
    { behandelaar, recordmanager }[_].rol in user.rollen
    zaaktype_allowed == true
}

default heropenen := false
heropenen {
    recordmanager.rol in user.rollen
    zaaktype_allowed == true
}

default wijzigenZaakdata := false
wijzigenZaakdata {
    recordmanager.rol in user.rollen
    zaaktype_allowed == true
}

default wijzigenDoorlooptijd := false
wijzigenDoorlooptijd {
    recordmanager.rol in user.rollen
    zaaktype_allowed == true
}
