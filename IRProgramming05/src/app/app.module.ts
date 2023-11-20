import { APP_INITIALIZER, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { Routes, RouterModule } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatIconRegistry } from "@angular/material/icon";
import { DomSanitizer } from "@angular/platform-browser";
import { MatSortModule } from '@angular/material/sort';
import { MatSort } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SearchPageComponent } from './search-page/search-page.component';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { NewSearchComponent } from './new-search/new-search.component';
import { NgbAlertModule, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { HttpClientModule } from '@angular/common/http';
import { AppConfigService } from './app-config.service';
import { NewSearchService } from './new-search/new-search.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HighlightSearchPipe } from './new-search/highlightPipe';
import { HighlightDirective } from './highlight.directive';
import { NgxPaginationModule } from 'ngx-pagination';

const appRoutes: Routes = [
  { path: '', component: NewSearchComponent },
];

@NgModule({
  declarations: [
    AppComponent,
    SearchPageComponent,
    HighlightSearchPipe,
    NewSearchComponent,
    HighlightDirective
  ],
  imports: [
    RouterModule.forRoot(appRoutes),
    NgbAlertModule,
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    FormsModule,
    FontAwesomeModule,
    MatTableModule,
    MatPaginatorModule,
    NgxPaginationModule,
    NgbModule,
    HttpClientModule
  ],
  exports: [RouterModule],
  providers: [
  NewSearchService ],
  bootstrap: [AppComponent]
})
export class AppModule { }
