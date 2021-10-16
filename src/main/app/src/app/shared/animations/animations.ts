/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {animate, state, style, transition, trigger} from '@angular/animations';

export const rotate180 = trigger('rotated180', [
    state('default', style({transform: 'rotate(0)'})),
    state('rotated', style({transform: 'rotate(-180deg)'})),
    transition('rotated => default', animate('300ms ease-out')),
    transition('default => rotated', animate('300ms ease-out'))
]);

export const sideNavToggle = trigger('sideNavToggle', [
    state('close', style({'width': '{{width}}'}), {params: {width: '65px'}}),
    state('open', style({'min-width': '200px'})),
    transition('close <=> open', animate('200ms ease-in'))
]);

export const detailExpand = trigger('detailExpand', [
    state('collapsed', style({height: '0px', minHeight: '0'})),
    state('expanded', style({height: '*'})),
    transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)'))
]);
