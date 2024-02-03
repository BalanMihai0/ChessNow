class User {
    constructor(
      email,
      firstName,
      lastName,
      username,
      password,
      confirmPass,
      phone,
      country
    ) {
      this.email = email;
      this.firstName = firstName;
      this.lastName = lastName;
      this.username = username;
      this.password = password;
      this.confirmPass = confirmPass;
      this.phone = phone;
      this.country = country;
    }
  }
  
  export default User;
  