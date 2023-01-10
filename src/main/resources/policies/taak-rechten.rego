package net.atos.zac.taak

import future.keywords
import data.net.atos.zac.rol.behandelaar
import data.net.atos.zac.rol.recordmanager
import input.user
import input.taak

taak_rechten := {
    "lezen": lezen,
    "wijzigen": wijzigen,
    "toekennen": toekennen,
}

default zaaktype_allowed := false
zaaktype_allowed {
    not user.zaaktypen
}
zaaktype_allowed {
    taak.zaaktype in user.zaaktypen
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
