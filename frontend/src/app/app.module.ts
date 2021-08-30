import { MaterialElevationDirective } from './components/library-list/material-elevation.directive';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatIconModule} from "@angular/material/icon";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {MatCardModule} from "@angular/material/card";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { HomeComponent } from './components/home/home.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {BasicAuthInterceptor} from "./auth/basic-http-auth.interceptor";
import {ErrorInterceptor} from "./auth/error.interceptor";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatMenuModule} from "@angular/material/menu";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatListModule} from "@angular/material/list";
import {MatGridListModule} from "@angular/material/grid-list";
import { SignupComponent } from './components/signup/signup.component';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import { MyReservationsComponent } from './components/my-reservations/my-reservations.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { AddLibraryComponent } from './components/add-library/add-library.component';
import { LibraryComponent } from './components/library/library.component';
import { CalendarComponent } from './components/calendar/calendar.component';
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MAT_DATE_LOCALE, MatNativeDateModule} from '@angular/material/core';
import { LeafletModule } from '@asymmetrik/ngx-leaflet';
import { MapComponent } from './components/map/map.component';
import { ReservationsBoxComponent } from './components/reservations-box/reservations-box.component';
import {MatCheckboxModule} from "@angular/material/checkbox";
import {CalendarHeader} from "./components/calendar/calendar.header.component";
import {MatChipsModule} from "@angular/material/chips";
import { AdminReservationsBoxComponent } from './components/admin-reservations-box/admin-reservations-box.component';
import {MatTabsModule} from "@angular/material/tabs";
import {MatTableModule} from "@angular/material/table";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatSliderModule} from "@angular/material/slider";
import {QRCodeModule} from "angularx-qrcode";
import { QrcodeComponent } from './components/qrcode/qrcode.component';
import {MatBottomSheet, MatBottomSheetModule} from "@angular/material/bottom-sheet";
import { LibraryListComponent } from './components/library-list/library-list.component';
import { FlexLayoutModule } from '@angular/flex-layout';
import { SearchBarComponent } from './components/search-bar/search-bar.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    SignupComponent,
    MyReservationsComponent,
    PageNotFoundComponent,
    AddLibraryComponent,
    LibraryComponent,
    CalendarComponent,
    CalendarHeader,
    MapComponent,
    ReservationsBoxComponent,
    AdminReservationsBoxComponent,
    QrcodeComponent,
    LibraryListComponent,
    SearchBarComponent,
    MaterialElevationDirective
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        MatButtonModule,
        MatCardModule,
        ReactiveFormsModule,
        MatToolbarModule,
        MatMenuModule,
        MatProgressSpinnerModule,
        MatListModule,
        MatGridListModule,
        MatSnackBarModule,
        MatDatepickerModule,
        MatNativeDateModule,
        LeafletModule,
        MatCheckboxModule,
        MatChipsModule,
        MatTabsModule,
        MatTableModule,
        MatPaginatorModule,
        MatSliderModule,
        FormsModule,
        QRCodeModule,
        MatBottomSheetModule,
        FlexLayoutModule
    ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'it-IT' },
    { provide: HTTP_INTERCEPTORS, useClass: BasicAuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
