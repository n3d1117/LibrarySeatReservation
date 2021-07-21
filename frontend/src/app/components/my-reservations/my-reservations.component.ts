import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {first} from "rxjs/operators";
import {Reservation} from "../../models/reservation.model";
import {ReservationService} from "../../services/reservation.service";
import {AuthenticationService} from "../../services/authentication.service";

@Component({
  selector: 'app-my-reservations',
  templateUrl: './my-reservations.component.html',
  styleUrls: ['./my-reservations.component.css']
})
export class MyReservationsComponent implements OnInit {

  loading = false;
  reservations: Reservation[] = [];
  error = '';

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private reservationService: ReservationService
  ) { }

  ngOnInit(): void {
    this.loading = true;
    this.reservationService.allByUser(this.authenticationService.currentUserValue.id)
      .pipe(first()).subscribe(reservations => {
      this.loading = false;
      this.reservations = reservations;
    }, error => {
      this.loading = false;
      this.error = error;
    });
  }

}
