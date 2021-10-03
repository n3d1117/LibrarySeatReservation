export class User {
  id: number;
  email: string;
  name: string;
  surname: string;
  roles: string[];
  jwt: string;

  constructor(id: number, email: string, name: string, surname: string, roles: string[], jwt: string) {
    this.id = id;
    this.email = email;
    this.name = name;
    this.surname = surname;
    this.roles = roles;
    this.jwt = jwt;
  }
}
