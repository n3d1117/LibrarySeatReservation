import {Component, Inject, OnInit} from '@angular/core';
import {MAT_BOTTOM_SHEET_DATA} from "@angular/material/bottom-sheet";
import {Reservation} from "../../models/reservation.model";
import {DateUtilityService} from "../../services/date-utility.service";

@Component({
  selector: 'app-qrcode',
  templateUrl: './qrcode.component.html',
  styleUrls: ['./qrcode.component.css']
})
export class QrcodeComponent implements OnInit {

  constructor(
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: {reservation: Reservation},
    private dateService: DateUtilityService
  ) { }

  ngOnInit(): void {
  }

  libraryName(): string {
    return this.data.reservation.libraryName;
  }

  humanReadableDate(): string {
    return this.dateService.dateToHumanReadableString(
      this.dateService.stringToDate(this.data.reservation.datetime)
    )
  }

  humanReadableHours(): string {
    return this.dateService.dateToHumanReadableHourString(
      this.dateService.stringToDate(this.data.reservation.datetime)
    )
  }
}
