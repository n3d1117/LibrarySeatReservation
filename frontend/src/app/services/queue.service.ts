import {Injectable} from '@angular/core';
import {webSocket} from "rxjs/webSocket";

@Injectable({
  providedIn: 'root'
})
export class QueueService {

  socket$ = webSocket("ws://localhost:8080/gateway/queue") // todo parametrize

  constructor() {
  }
}
