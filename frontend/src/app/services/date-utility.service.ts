import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DateUtilityService {

  constructor() {
  }

  /**
   * Returns a Date object representing the day before current one
   */
  yesterday(): Date {
    return new Date(Date.now() - 86400000)
  }

  /**
   * Returns true if the specified string date is older than today
   */
  isOlderThanToday(first: string): boolean {
    return this.stringToDate(first) < this.yesterday();
  }

  /**
   * Converts the two strings into dates and returns the ms difference between them
   */
  difference(first: string, second: string): number {
    return this.stringToDate(first).getTime() - this.stringToDate(second).getTime()
  }

  /**
   * Converts the string into a Date object
   */
  stringToDate(date: string): Date {
    return new Date(date.replace(' ', 'T'));
  }

  /**
   * Returns a human readable version of the specified date, e.g: "Lunedì 27 Settembre"
   */
  dateToHumanReadableString(date: Date): string {
    return date.toLocaleDateString('it',
      {weekday: "long", month: "long", day: "numeric"}
    ).split(' ')
      .map(word => word.charAt(0).toUpperCase() + word.substring(1))
      .join(' ');
  }

  /**
   * Returns human readable hour string from specified date, e.g: "08:00"
   */
  dateToHumanReadableHourString(date: Date): string {
    return date.getHours() + ":00";
  }

  /**
   * Converts the specified date into a "yyyy-MM-dd HH:mm:ss" formatted string
   * @param date the date object
   * @param isMorning determines the date hours to use: 08:00 if true, 13:00 if false
   */
  prepareDateForBackend(date: Date, isMorning: boolean): string {
    date.setHours(isMorning ? 8 : 13);
    return new Date(
      date.getTime() - (date.getTimezoneOffset() * 60000)
    ).toISOString().replace('T', ' ').split('.')[0];
  }
}
