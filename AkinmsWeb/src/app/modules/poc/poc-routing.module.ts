import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PocComponent } from './containers/poc/poc.component';

const routes: Routes = [
  {
    path:'',
    component: PocComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PocRoutingModule { }
