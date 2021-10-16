/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Injector, Pipe, PipeTransform, Type} from '@angular/core';
import {AppModule} from '../../../app.module';

/**
 * Een pipe om dynamisch een andere pipe in te laden.
 * Via deze pipe hoef je geen rekening te houden met specifieke constructor parameters van andere pipe implementaties
 */
@Pipe({
    name: 'dynamic'
})
export class DynamicPipe implements PipeTransform {

    transform(value: any, requiredPipe: Type<PipeTransform>, args?: any): any {
        const injector = Injector.create({
            name: 'DynamicPipe',
            parent: AppModule.injector,
            providers: [
                {provide: requiredPipe}
            ]
        });
        const pipe = injector.get(requiredPipe);
        return pipe.transform(value, args);
    }
}
