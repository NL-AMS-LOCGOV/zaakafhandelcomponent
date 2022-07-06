package net.atos.zac

import future.keywords
import data.net.atos.zac.rollen
import input.user

zaaktypen[rol_zaaktypen] {
    some rol
    rollen[rol].id in user.rollen
    rol_zaaktypen := rollen[rol].zaaktypen
}
