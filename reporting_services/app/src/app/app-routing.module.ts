import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { IterationComponent } from './iteration/iteration.component';
import { StateComponent } from './iteration/state/state.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
  },
  {
    path: 'iterations/:iterationId',
    component: IterationComponent,
  },
  {
    path: 'states/:sequenceItemId',
    component: StateComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
