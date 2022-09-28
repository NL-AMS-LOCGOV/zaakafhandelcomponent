package net.atos.zac.zaaktype

import future.keywords
import data.net.atos.zac.domein.domeinen
import input.user

zaaktypen[domein_zaaktypen] {
    domein_zaaktypen := union({ zaaktype | domeinen[i].rol in user.rollen; zaaktype := domeinen[i].zaaktypen })
}
