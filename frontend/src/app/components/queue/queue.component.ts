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

  ngOnDestroy(): void {
    this.queueService.socket$.complete();
  }

  ngOnInit(): void {

    if (!this.route.snapshot.queryParams['returnUrl']) {
      this.router.navigate(['home']);
    }
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'];

    this.queueService.socket$.subscribe(message => {
      const jsonString = JSON.stringify(message);
      const msg = JSON.parse(jsonString);

      if (msg.action == 'queue_size') {
        this.queueLength = msg.value;
      } else if (msg.action == 'queue_size_decrease') {
        this.queueLength--;

        // end of queue!
        if (this.queueLength <= 0) {
          this.router.navigate([this.returnUrl]);
        }
      }
    })
  }

}