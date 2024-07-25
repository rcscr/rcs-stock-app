import { UserInfo } from "./types";

export default class StoreManager {

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