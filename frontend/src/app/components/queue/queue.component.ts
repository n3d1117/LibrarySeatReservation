import {environment} from '../../../environments/environment';
import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {QueueService} from "../../services/queue.service";

@Component({
  selector: 'app-queue',
  templateUrl: './queue.component.html',
  styleUrls: ['./queue.component.css']
})
export class QueueComponent implements OnInit {

  public queueLength = 0;
  returnUrl = '/';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private queueService: QueueService
  ) {
  }

  /**
   * Disconnect from the WebSocket upon destruction of the component
   */
  ngOnDestroy(): void {
    this.queueService.socket$.complete();
  }

  ngOnInit(): void {

    // If there's no returnUrl in query params, something went wrong
    if (!this.route.snapshot.queryParams['returnUrl']) {
      this.router.navigate(['home']);
      return;
    }
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'];

    // Subscribe to the queue web socket and listen for messages
    this.queueService.socket$.subscribe(message => {
      const jsonString = JSON.stringify(message);
      const msg = JSON.parse(jsonString);

      if (msg.type == 'queue_size') {
        // Update the component with current queue length
        this.queueLength = msg.value;
      } else if (msg.type == 'queue_size_decrease') {
        // Decrease queue length by one
        this.queueLength--;

        // Check if end of queue
        if (this.queueLength <= 0)
          this.router.navigate([this.returnUrl]);
      }
    })
  }

  /**
   * Returns the number of seconds the connected user is expected to wait
   *    n = queueLength * estimated wait time per user
   */
  waitTimeSeconds(): number {
    return this.queueLength * environment.ESTIMATED_WAITING_TIME_SECONDS_PER_USER;
  }

  /**
   * Formats the specified seconds into a human readable string, e.g: "1 minuto e 30 secondi"
   */
  formatTime(seconds: number): string {
    const date = new Date(seconds * 1000);
    let str = ''
    if (date.getUTCHours() > 0) {
      str += date.getUTCHours() + (date.getUTCHours() == 1 ? " ora" : " ore");
    }
    if (date.getUTCMinutes() > 0) {
      if (date.getUTCHours() > 0)
        str += ", "
      str += date.getUTCMinutes() + (date.getUTCMinutes() == 1 ? " minuto" : " minuti");
    }
    if (date.getUTCSeconds() > 0) {
      if (date.getUTCMinutes() > 0)
        str += " e "
      str += date.getUTCSeconds() + " secondi";
    }
    return str;
  }

}
