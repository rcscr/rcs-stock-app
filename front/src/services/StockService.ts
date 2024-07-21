export interface StockPrice {
  symbol: string;
  currency: string;
  price: number;
  percentageChange: number;
}

export interface StockInfo {
  currency: string;
  description: string;
  displaySymbol: string;
  figi: string;
  isin: string;
  mic: string;
  symbol: string;
  symbol2: string;
  type: string;
}

export interface StockWithFollowers {
  symbol: string;
  numberOfFollowers: number;
}

export interface UserStocks {
  username: string;
  stocks: string[];
}

export default class StockService {

  private readonly url;

  constructor(url: string) {
    this.url = url;
  }

  public getWebsocketUrl() {
    return this.url + '/stock-app-websocket';
  }

  public getWebsocketTopicUrl(symbol: string) {
    return '/topic/stocks/' + symbol;
  }

  public async getMyStocks() {
    const requestOptions = {
      method: 'GET',
      credentials: 'include' as RequestCredentials
    };

    const response = await fetch(this.url + '/my-stocks', requestOptions)
      .then(response => response.json());

    return response as UserStocks;
  }

  public async followStock(stock: string) {
    const requestOptions = {
      method: 'PUT',
      credentials: 'include' as RequestCredentials
    };

    const response = await fetch(this.url + '/my-stocks?stock=' + stock, requestOptions)
      .then(response => response.json());

    return response as UserStocks;
  }

  public async unfollowStock(stock: string) {
    const requestOptions = {
      method: 'DELETE',
      credentials: 'include' as RequestCredentials
    };

    const response = await fetch(this.url + '/my-stocks?stock=' + stock, requestOptions)
      .then(response => response.json());

    return response as UserStocks;
  }

  public async searchStocks(search: string) {
    const requestOptions = {
      method: 'GET'
    };

    const response = await fetch(this.url + '/stocks?limit=10&search=' + encodeURIComponent(search.trim()), requestOptions)
      .then(response => response.json());

    return response as StockInfo[];
  }

  public async getStocksWithFollowers() {
    const requestOptions = {
      method: 'GET'
    };

    const response = await fetch(this.url + '/stocks-with-followers', requestOptions)
      .then(response => response.json());

    return response as StockWithFollowers[];
  }
}