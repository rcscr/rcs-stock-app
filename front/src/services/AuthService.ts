export default class AuthService {

  private readonly url;

  constructor(url: string) {
    this.url = url;
  }

  public login(username, password) {
    const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `username=${username}&password=${password}`,
        credentials: 'include' as RequestCredentials
    };

    return fetch(this.url + '/login', requestOptions)
      .then(response => response.ok);
  }

  public logout() {
    const requestOptions = {
        method: 'POST',
        credentials: 'include' as RequestCredentials
    };

    return fetch(this.url + '/logout', requestOptions)
      .then(response => response.ok);
  }

  public register(username, password) {
    const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `username=${username}&password=${password}`,
        credentials: 'include' as RequestCredentials
    };

    return fetch(this.url + '/register', requestOptions);                                                                                                                                     
  }

  public async authenticate() {
    const requestOptions = {
      method: 'GET',
      credentials: 'include' as RequestCredentials
    };

    return fetch(this.url + '/authenticate', requestOptions);
  }
}