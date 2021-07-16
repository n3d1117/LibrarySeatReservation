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
    private authenticationService: AuthenticationService
  ) {
    this.currentUser = null;
    this.authenticationService.currentUser.subscribe(user => this.currentUser = user);
  }

  isAdmin(): boolean {
    if (!this.currentUser)
      return false
    return this.currentUser?.roles.includes('ADMIN');
  }

  myReservations(): void {
    if (this.isAdmin()) {
      // todo
    } else {
      this.router.navigate(['my-reservations']);
    }
  }

  logout(): void {
    this.authenticationService.logout();
    this.router.navigate(['/login']);
  }
}
