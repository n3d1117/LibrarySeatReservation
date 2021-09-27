import {MaterialElevationDirective} from './components/library-list/material-elevation.directive';
import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {LoginComponent} from './components/login/login.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HomeComponent} from './components/home/home.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {BasicAuthInterceptor} from "./auth/basic-http-auth.interceptor";
import {ErrorInterceptor} from "./auth/error.interceptor";
import {SignupComponent} from './components/signup/signup.component';
import {MyReservationsComponent} from './components/my-reservations/my-reservations.component';
import {PageNotFoundComponent} from './components/page-not-found/page-not-found.component';
import {AddLibraryComponent} from './components/add-library/add-library.component';
import {LibraryComponent} from './components/library/library.component';
import {CalendarComponent} from './components/calendar/calendar.component';
import {LeafletModule} from '@asymmetrik/ngx-leaflet';
import {MapComponent} from './components/map/map.component';
import {ReservationsBoxComponent} from './components/reservations-box/reservations-box.component';
import {CalendarHeader} from "./components/calendar/calendar.header.component";
import {AdminReservationsBoxComponent} from './components/admin-reservations-box/admin-reservations-box.component';
import {QRCodeModule} from "angularx-qrcode";
import {QrcodeComponent} from './components/qrcode/qrcode.component';
import {LibraryListComponent} from './components/library-list/library-list.component';
import {FlexLayoutModule} from '@angular/flex-layout';
import {SearchBarComponent} from './components/search-bar/search-bar.component';
import {MAT_DATE_LOCALE} from '@angular/material/core';
import {MaterialModule} from './material/material.module';
import {AdminSkipQueueInterceptor} from "./auth/admin-skip-queue.interceptor";
import {ConfirmDialogComponent} from './components/confirm-dialog/confirm-dialog.component';
import {QueueComponent} from './components/queue/queue.component';
import {SplashScreenComponent} from './components/splash-screen/splash-screen.component';

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
    MaterialElevationDirective,
    ConfirmDialogComponent,
    QueueComponent,
    SplashScreenComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    ReactiveFormsModule,
    LeafletModule,
    FormsModule,
    QRCodeModule,
    FlexLayoutModule,
    MaterialModule
  ],
  providers: [
    {provide: MAT_DATE_LOCALE, useValue: 'it-IT'},
    {provide: HTTP_INTERCEPTORS, useClass: BasicAuthInterceptor, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: AdminSkipQueueInterceptor, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true},
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
