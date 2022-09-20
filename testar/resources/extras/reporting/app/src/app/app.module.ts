import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { IterationComponent } from './iteration/iteration.component';
import { IterationsListComponent } from './home/iterations-list/iterations-list.component';
import { LatestIterationComponent } from './home/latest-iteration/latest-iteration.component';
import { ContentContainerComponent } from './shared/content-container/content-container.component';
import { StateComponent } from './iteration/state/state.component';
import { ReportInfoComponent } from './home/report-info/report-info.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    IterationComponent,
    IterationsListComponent,
    LatestIterationComponent,
    ContentContainerComponent,
    StateComponent,
    ReportInfoComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
