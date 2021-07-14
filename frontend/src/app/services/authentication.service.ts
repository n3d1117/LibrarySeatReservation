import { Injectable } from '@angular/core';
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

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<User | null>(JSON.parse(<string>localStorage.getItem('currentUser')));
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): User {
    return <User>this.currentUserSubject.value;
  }

  login(email: string, password: string): Observable<User> {
    return this.http.post<User>(`${environment.REST_API_URL}/users/login?email=${email}&password=${password}`, {})
      .pipe(map(user => {
        user.authData = window.btoa(email + ':' + password);
        localStorage.setItem('currentUser', JSON.stringify(user));
        this.currentUserSubject.next(user);
        return user;
      }));
  }

  signup(name: string, surname: string, email: string, password: string, admin: boolean): Observable<User> {
    const options = { headers: {'Content-Type': 'application/json'} };
    const roles: string[] = admin ? ['BASIC', 'ADMIN'] : ['BASIC'];
    const user = { email: email, name: name, surname: surname, password: password, roles: roles };
    return this.http.post<User>(`${environment.REST_API_URL}/users/signup`, user, options);
  }

  // all(): Observable<User[]> {
  //   return this.http.get<User[]>(`${environment.REST_API_URL}/users/`)
  // }

  logout(): void {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }
}
