export class Library {
  id: number;
  name: string;
  address: string;
  capacity: number;

  constructor(id: number, name: string, address: string, capacity: number) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.capacity = capacity;
  }
}
