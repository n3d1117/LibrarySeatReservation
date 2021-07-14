export class User {
  id: number;
  email: string;
  name: string;
  surname: string;
  roles: string[];
  authData?: string;

  constructor(id: number, email: string, name: string, surname: string, roles: string[]) {
    this.id = id;
    this.email = email;
    this.name = name;
    this.surname = surname;
    this.roles = roles;
  }

}
