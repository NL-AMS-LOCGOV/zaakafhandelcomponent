package net.atos.zac.zaken

import future.keywords
import data.net.atos.zac.rollen
import input.user

zaken_acties := {
    "verdelen_en_vrijgeven": verdelen_en_vrijgeven,
    "toekennen_aan_mijzelf": toekennen_aan_mijzelf
}

default verdelen_en_vrijgeven := false
verdelen_en_vrijgeven {
    rollen.werkverdeler.id in user.rollen
}

default toekennen_aan_mijzelf := false
toekennen_aan_mijzelf {
    rollen.zaakbehandelaar.id in user.rollen
}
