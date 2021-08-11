import { Component } from '@angular/core';
import {User} from "./models/user.model";
import {AuthenticationService} from "./services/authentication.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  currentUser: User | null;

  constructor(
    private router: Router,
    public authenticationService: AuthenticationService
  ) {
    this.currentUser = null;
    this.authenticationService.currentUser.subscribe(user => this.currentUser = user);
  }

  home(): void {
    this.router.navigate(['home']);
  }

  myReservations(): void {
    this.router.navigate(['my-reservations']);
  }

  logout(): void {
    this.authenticationService.logout();
    this.router.navigate(['/login']);
  }
}
