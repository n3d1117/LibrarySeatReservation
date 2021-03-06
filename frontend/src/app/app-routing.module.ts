import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from "./components/login/login.component";
import {LoginProtected} from "./auth/login-protect";
import {HomeComponent} from "./components/home/home.component";
import {SignupComponent} from "./components/signup/signup.component";
import {MyReservationsComponent} from "./components/my-reservations/my-reservations.component";
import {PageNotFoundComponent} from "./components/page-not-found/page-not-found.component";
import {AddLibraryComponent} from "./components/add-library/add-library.component";
import {AdminOnly} from "./auth/admin-only";
import {LibraryComponent} from "./components/library/library.component";
import {QueueComponent} from "./components/queue/queue.component";
import {QueueErrorComponent} from "./components/queue-error/queue-error.component";

const routes: Routes = [
  {path: 'home', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'signup', component: SignupComponent},
  {path: 'libraries/:id', component: LibraryComponent},
  {path: 'my-reservations', component: MyReservationsComponent, canActivate: [LoginProtected]},
  {path: 'queue', component: QueueComponent, canActivate: [LoginProtected]},
  {path: 'queue-error', component: QueueErrorComponent},

  {path: 'admin/add-library', component: AddLibraryComponent, canActivate: [AdminOnly]},

  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {path: '**', component: PageNotFoundComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
