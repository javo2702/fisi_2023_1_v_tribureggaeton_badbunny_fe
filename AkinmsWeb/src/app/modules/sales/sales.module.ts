import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SalesRoutingModule } from './sales-routing.module';
import { SalesComponent } from './containers/sales/sales.component';
import { SharedModule } from "../../shared/shared.module";


@NgModule({
    declarations: [
        SalesComponent
    ],
    imports: [
        CommonModule,
        SalesRoutingModule,
        SharedModule
    ]
})
export class SalesModule { }
