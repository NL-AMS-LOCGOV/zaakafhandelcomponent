package net.atos.zac.taken

import future.keywords
import data.net.atos.zac.rol.behandelaar
import data.net.atos.zac.rol.coordinator
import input.user

taken_acties := {
    "verdelen_en_vrijgeven": verdelen_en_vrijgeven,
    "toekennen_aan_mijzelf": toekennen_aan_mijzelf
}

default verdelen_en_vrijgeven := false
verdelen_en_vrijgeven {
    coordinator.rol in user.rollen
}

default toekennen_aan_mijzelf := false
toekennen_aan_mijzelf {
    behandelaar.rol in user.rollen
}
