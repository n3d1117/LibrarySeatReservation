export class Library {
  id: number;
  name: string;
  imgFilename: string;
  address: string;
  capacity: number;

  constructor(id: number, name: string, imgFilename: string, address: string, capacity: number) {
    this.id = id;
    this.name = name;
    this.imgFilename = imgFilename
    this.address = address;
    this.capacity = capacity;
  }
}
