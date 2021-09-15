import { environment } from './../../environments/environment';
import {Injectable} from '@angular/core';
import {webSocket} from "rxjs/webSocket";

@Injectable({
  providedIn: 'root'
})
export class ConcurrentUsersService {

  socket$ = webSocket(environment.CONCURRENT_USERS_SOCKET);

  constructor() {
  }
}
