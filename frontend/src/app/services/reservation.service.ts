import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {Reservation} from "../models/reservation.model";
import {ReservationsDailyAggregate} from "../models/reservations_daily_aggregate";

@Injectable({
  providedIn: 'root'
})
export class ReservationService {

  private readonly url;

  constructor(private http: HttpClient) {
    this.url = `${environment.REST_API_URL}/reservations`;
  }

  all(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(this.url)
  }

  allByUser(userId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(this.url + `/user/${userId}`)
  }

  allByLibrary(libraryId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(this.url + `/library/${libraryId}`)
  }

  allByLibraryAndDate(libraryId: number, year: number, month: number, day: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(this.url + `/library/${libraryId}/${year}/${month}/${day}`)
  }

  aggregateByLibraryAndMonth(libraryId: number, year: number, month: number): Observable<ReservationsDailyAggregate[]> {
    return this.http.get<ReservationsDailyAggregate[]>(this.url + `/stats/library/${libraryId}/${year}/${month}`)
  }

  add(userId: number, libraryId: number, datetime: string): Observable<Reservation> {
    const options = { headers: {'Content-Type': 'application/json'} };
    const reservation = { userId: userId, libraryId: libraryId, datetime: datetime };
    return this.http.post<Reservation>(`${this.url}/add`, reservation, options);
  }

  delete(reservationId: number) {
    return this.http.delete(`${this.url}/delete/${reservationId}`)
  }

}
