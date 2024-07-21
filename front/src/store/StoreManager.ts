import { UserInfo } from "./types";

export default class StockAppManager {

  private readonly store;

  constructor(store) {
    this.store = store;
  }

  public setUserInfo(userInfo: UserInfo) {
    this.store.dispatch({ 
      type: 'set-user-info', 
      userInfo 
    });
  }
}