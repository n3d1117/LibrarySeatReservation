import {Component, Input, OnInit, Output, ViewChild} from '@angular/core';
import {MatCalendar, MatCalendarCellClassFunction} from "@angular/material/datepicker";
import {CalendarHeader} from "./calendar.header.component";
import {Reservation} from "../../models/reservation.model";
import {ReservationService} from "../../services/reservation.service";
import {Library} from "../../models/library.model";
import {first} from "rxjs/operators";
import {EventEmitter} from '@angular/core';
import {DateUtilityService} from "../../services/date-utility.service";
import {ReservationsDailyAggregate} from "../../models/reservations_daily_aggregate";

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit {

  @ViewChild(MatCalendar) calendar!: MatCalendar<Date>;
  calendarHeader = CalendarHeader;
  aggregateReservationsByMonth: ReservationsDailyAggregate[] = [];
  @Input() library!: Library;

  @Output() selectedDate = new EventEmitter<Date>();
  @Output() dayReservations = new EventEmitter<Reservation[]>();

  todayDate = new Date();

  constructor(
    private reservationService: ReservationService,
    private dateService: DateUtilityService
  ) {
  }

  ngOnInit(): void {
    this.onMonthChange(new Date());
  }

  /**
   * Called for every day in the calendar view; returns the CSS class to apply for each of them.
   * Possible classes: full/available/empty
   */
  dateClass: MatCalendarCellClassFunction<Date> = (cellDate) => {
    if (cellDate < this.dateService.yesterday()) {
      return ''
    }
    const dayReservations = this.aggregateReservationsByMonth.filter(stat =>
      this.dateService.stringToDate(stat.date).getDate() == cellDate.getDate()
    );
    if (dayReservations.length == 0)
      return 'available'
    const countMorning = dayReservations[0].countMorning;
    const countAfternoon = dayReservations[0].countAfternoon;
    if (countMorning >= this.library.capacity && countAfternoon >= this.library.capacity)
      return 'full'
    return 'available';
  }

  /**
   * Called every time user changes month in the calendar view.
   * Will request reservation data from the API for the currently selected month.
   */
  onMonthChange(date: Date): void {
    const selectedDate = date ? date : this.todayDate;
    this.aggregateReservationsByMonth = [];
    const year = selectedDate.getFullYear();
    const month = selectedDate.getMonth() + 1; // In Typescript, months start from 0
    this.reservationService.aggregateByLibraryAndMonth(this.library.id, year, month).pipe(first()).subscribe(stats => {
      this.aggregateReservationsByMonth = stats;
      this.calendar.updateTodaysDate();
    }, error => {
      console.log(error);
    });
  }

  /**
   * Called when a date is selected in the calendar view.
   * Will request reservations for selected library and day from the API.
   */
  onDateSelected(date: Date | null): void {
    if (date) {
      const year = date.getFullYear();
      const month = date.getMonth() + 1; // In Typescript, months start from 0
      const day = date.getDate();
      this.reservationService.allByLibraryAndDate(this.library.id, year, month, day).pipe(first()).subscribe(reservations => {
        this.selectedDate.emit(date);
        this.dayReservations.emit(reservations);
      }, error => {
        console.log(error);
      });
    }
  }

}
