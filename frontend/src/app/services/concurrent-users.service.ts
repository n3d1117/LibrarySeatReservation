import {Injectable} from '@angular/core';
import {webSocket} from "rxjs/webSocket";

@Injectable({
  providedIn: 'root'
})
export class ConcurrentUsersService {

  socket$ = webSocket("ws://localhost:8080/gateway/concurrent-users") // todo parametrize

  constructor() {
  }
}
