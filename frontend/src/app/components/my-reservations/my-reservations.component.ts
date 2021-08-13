import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {first} from "rxjs/operators";
import {Reservation} from "../../models/reservation.model";
import {ReservationService} from "../../services/reservation.service";
import {AuthenticationService} from "../../services/authentication.service";
import {PageEvent} from "@angular/material/paginator";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatBottomSheet} from "@angular/material/bottom-sheet";
import {QrcodeComponent} from "../qrcode/qrcode.component";
import {DateUtilityService} from "../../services/date-utility.service";

@Component({
  selector: 'app-my-reservations',
  templateUrl: './my-reservations.component.html',
  styleUrls: ['./my-reservations.component.css']
})
export class MyReservationsComponent implements OnInit {

  loading = false;
  reservations: Reservation[] = [];
  error = '';

  displayedColumns: string[] = ['hour', 'datetime', 'libraryName', 'qr', 'action'];
  lowValue = 0;
  highValue = 5;

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private reservationService: ReservationService,
    private snackBar: MatSnackBar,
    private bottomSheet: MatBottomSheet,
    private dateService: DateUtilityService
  ) { }

  pastReservations(): Reservation[] {
    return this.reservations.filter((item) => {
      return this.dateService.isOlderThanToday(item.datetime);
    }).sort((a, b) => {
      return this.dateService.sort(a.datetime, b.datetime);
    })
  }

  futureReservations(): Reservation[] {
    return this.reservations.filter((item) => {
      return !this.dateService.isOlderThanToday(item.datetime);
    }).sort((a, b) => {
      return this.dateService.sort(a.datetime, b.datetime);
    })
  }

  ngOnInit(): void {
    this.loading = true;
    this.reservationService.allByUser(this.authenticationService.currentUserValue.id)
      .pipe(first()).subscribe(reservations => {
      this.reservations = reservations;
      this.loading = false;
    }, error => {
      this.loading = false;
      this.error = error;
    });
  }

  public getPaginatorData(event: PageEvent): PageEvent {
    this.lowValue = event.pageIndex * event.pageSize;
    this.highValue = this.lowValue + event.pageSize;
    return event;
  }

  deleteReservation(reservationId: number): void {
    this.reservationService.delete(reservationId)
      .pipe(first())
      .subscribe(() => {
        this.snackBar.open('Prenotazione cancellata!', '', {duration: 5000});
      }, error => {
        console.log(error);
      });
  }

  openQrCodeBottomSheet(reservation: Reservation): void {
      this.bottomSheet.open(QrcodeComponent, {
        data: { reservation: reservation }
      });
  }

  humanReadable(date: string): string {
    return this.dateService.dateToHumanReadableString(
      this.dateService.stringToDate(date)
    )
  }

  humanReadableHours(date: string): string {
    return this.dateService.dateToHumanReadableHourString(
      this.dateService.stringToDate(date)
    )
  }

}
