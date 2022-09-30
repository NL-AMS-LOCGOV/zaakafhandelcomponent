package net.atos.zac.overig

import future.keywords
import data.net.atos.zac.rol.behandelaar
import data.net.atos.zac.rol.coordinator
import data.net.atos.zac.rol.recordmanager
import data.net.atos.zac.rol.beheerder
import input.user

overig_acties := {
    "starten_zaak": starten_zaak,
    "beheren": beheren,
    "zoeken": zoeken
}

default starten_zaak := true
starten_zaak {
    behandelaar.rol in user.rollen
}

default beheren:= false
beheren {
    beheerder.rol in user.rollen
}

default zoeken := false
zoeken {
    { behandelaar, coordinator, recordmanager }[_].rol in user.rollen
}
