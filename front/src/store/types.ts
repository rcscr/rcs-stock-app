import { UserStocks } from "../services/StockService";

export interface State {
  userInfo: UserInfo;
}

export interface UserInfo extends UserStocks {
  // more stuff can be added here
}