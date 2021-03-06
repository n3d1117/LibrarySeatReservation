import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from "rxjs";
import {User} from "../models/user.model";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser: Observable<User | null>;
  private readonly url;

  constructor(private http: HttpClient) {

    // If a user was previously saved in the browser's local storage, use it directly
    this.currentUserSubject = new BehaviorSubject<User | null>(JSON.parse(<string>localStorage.getItem('currentUser')));

    this.currentUser = this.currentUserSubject.asObservable();
    this.url = `${environment.GATEWAY_API_URL}/users`;
  }

  public get currentUserValue(): User {
    return <User>this.currentUserSubject.value;
  }

  /**
   * Returns true if a user is logged in and has the ADMIN role
   */
  isAdmin(): boolean {
    if (!this.currentUserValue)
      return false;
    return this.currentUserValue.roles.includes('ADMIN');
  }

  login(email: string, password: string): Observable<User> {
    return this.http.post<User>(`${this.url}/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`, {})
      .pipe(map(user => {
        // Set user in the browser's local storage
        localStorage.setItem('currentUser', JSON.stringify(user));
        // Notify observables
        this.currentUserSubject.next(user);
        return user;
      }));
  }

  signup(name: string, surname: string, email: string, password: string, admin: boolean): Observable<User> {
    const options = {headers: {'Content-Type': 'application/json'}};
    const roles: string[] = admin ? ['BASIC', 'ADMIN'] : ['BASIC'];
    const user = {email: email, name: name, surname: surname, password: password, roles: roles};
    return this.http.post<User>(`${this.url}/signup`, user, options);
  }

  logout(): void {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }
}
