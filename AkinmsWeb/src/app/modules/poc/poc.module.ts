import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PocRoutingModule } from './poc-routing.module';
import { PocComponent } from './containers/poc/poc.component';


@NgModule({
  declarations: [
    PocComponent
  ],
  imports: [
    CommonModule,
    PocRoutingModule
  ]
})
export class PocModule { }
