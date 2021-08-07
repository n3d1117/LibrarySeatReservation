import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {JsonArray} from "@angular/compiler-cli/ngcc/src/packages/entry_point";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class GeocodingService {

  constructor(private http: HttpClient) { }

  search(address: string): Observable<JsonArray> {
    const encodedAddress = encodeURIComponent(address).replace(/%20/g,'+');
    return this.http.get<JsonArray>(
      `https://nominatim.openstreetmap.org/search?q=${encodedAddress}&format=jsonv2`
    )
  }
}
