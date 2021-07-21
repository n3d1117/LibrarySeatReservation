export class Reservation {
  id: number;
  userId: number;
  libraryId: number;
  userName: string;
  userEmail: string;
  datetime: Date;

  constructor(id: number, userId: number, libraryId: number, userName: string, userEmail: string, datetime: Date) {
    this.id = id;
    this.userId = userId;
    this.libraryId = libraryId;
    this.userName = userName;
    this.userEmail = userEmail;
    this.datetime = datetime;
  }
}
