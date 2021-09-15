import { environment } from './../../environments/environment';
import {Injectable} from '@angular/core';
import {webSocket} from "rxjs/webSocket";

@Injectable({
  providedIn: 'root'
})
export class QueueService {

  socket$ = webSocket(environment.QUEUE_SOCKET);

  constructor() {
  }
}
