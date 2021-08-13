import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Library} from "../../models/library.model";
import {Reservation} from "../../models/reservation.model";
import {PageEvent} from "@angular/material/paginator";
import {ReservationService} from "../../services/reservation.service";
import {first} from "rxjs/operators";
import {MatSnackBar} from "@angular/material/snack-bar";
import {DateUtilityService} from "../../services/date-utility.service";

@Component({
  selector: 'app-admin-reservations-box',
  templateUrl: './admin-reservations-box.component.html',
  styleUrls: ['./admin-reservations-box.component.css']
})
export class AdminReservationsBoxComponent implements OnInit {

  @Input() library!: Library;
  @Input() selectedDate!: Date;
  @Input() dayReservations!: Reservation[];

  displayedColumns: string[] = ['userName', 'userEmail', 'action'];
  lowValue = 0;
  highValue = 5;

  constructor(
    private reservationService: ReservationService,
    private snackBar: MatSnackBar,
    private dateService: DateUtilityService
  ) { }

  ngOnInit(): void {
  }

  public getPaginatorData(event: PageEvent): PageEvent {
    this.lowValue = event.pageIndex * event.pageSize;
    this.highValue = this.lowValue + event.pageSize;
    return event;
  }

  morningReservations(): Reservation[] {
    if (!this.dayReservations)
      return []
    return this.dayReservations.filter(r => this.dateService.stringToDate(r.datetime).getHours() == 8);
  }

  afternoonReservations(): Reservation[] {
    if (!this.dayReservations)
      return []
    return this.dayReservations.filter(r => this.dateService.stringToDate(r.datetime).getHours() == 13);
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

  dateStringTitle(): string {
    return this.dateService.dateToHumanReadableString(this.selectedDate);
  }
}
