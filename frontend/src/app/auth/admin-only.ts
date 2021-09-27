import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import {AuthenticationService} from "../services/authentication.service";

@Injectable({providedIn: 'root'})
export class AdminOnly implements CanActivate {
  constructor(
    private router: Router,
    private authenticationService: AuthenticationService
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    // Only allow users with ADMIN privileges
    if (this.authenticationService.currentUserValue && this.isAdmin()) {
      return true;
    }
    // Logout
    this.authenticationService.logout();
    this.router.navigate(['/login']);
    return false;
  }

  private isAdmin(): boolean {
    return this.authenticationService.currentUserValue.roles.includes('ADMIN');
  }
}
