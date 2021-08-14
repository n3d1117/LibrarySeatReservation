export class ReservationsDailyAggregate {
  date: string;
  count: number;

  constructor(datetime: string, count: number) {
    this.date = datetime;
    this.count = count;
  }
}
