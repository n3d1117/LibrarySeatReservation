import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Library} from "../models/library.model";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class LibraryService {

  private readonly url;

  constructor(private http: HttpClient) {
    this.url = `${environment.GATEWAY_API_URL}/libraries`;
  }

  all(): Observable<Library[]> {
    return this.http.get<Library[]>(this.url)
  }

  find(id: number): Observable<Library> {
    return this.http.get<Library>(this.url + `/${id}`)
  }

  add(name: string, address: string, capacity: number): Observable<Library> {
    const options = { headers: {'Content-Type': 'application/json'} };
    const library = { name: name, address: address, capacity: capacity };
    return this.http.post<Library>(`${this.url}/add`, library, options);
  }

  delete(libraryId: number) {
    return this.http.delete(`${this.url}/delete/${libraryId}`);
  }

  update(library: Library) {
    return this.http.put(`${this.url}/update`, library);
  }
}
