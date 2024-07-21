import { connect } from "react-redux";
import Nav from './components/index/Nav';
import MyStocks from './components/following/MyStocks';
import './App.scss';
import AuthService from './services/AuthService';
import StockService from './services/StockService';
import StoreManager from './store/StoreManager';
import { State } from './store/types';
import SearchStocks from "./components/search/SearchStocks";
import LoginRegister from "./components/auth/LoginRegister";
import PopularStocks from "./components/popular/PopularStocks";
import { useEffect } from "react";

export interface ServicesConfig {
  authServiceUrl: string;
  stockServiceUrl: string;
}

interface Props extends State {
  storeManager: StoreManager;
  servicesConfig: ServicesConfig;
}

function App(props: Props) {

  document.title = 'RCS Stocks';

  const {
    storeManager,
    servicesConfig,
    userInfo
  } = props;

  const authService = new AuthService(servicesConfig.authServiceUrl);
  const stockService = new StockService(servicesConfig.stockServiceUrl);

  useEffect(() => {
    authService.authenticate()
      .then(async (response) => {
        if (response.ok) {
          storeManager.setUserInfo(await stockService.getMyStocks());
        }
      });
  }, []);

  const isLoggedIn = !!userInfo;

  return <div id="stock-app">
    <Nav userInfo={userInfo} authService={authService} storeManager={storeManager}/>
      
    { 
      !isLoggedIn && 
      <LoginRegister 
        authService={authService} 
        stockService={stockService} 
        storeManager={storeManager}/> 
    }

    { 
      isLoggedIn && 
      <>
        <PopularStocks 
          myStocks={userInfo.stocks}
          stockService={stockService}
          storeManager={storeManager}/> 
        <MyStocks 
          myStocks={userInfo.stocks} 
          stockService={stockService} 
          storeManager={storeManager}/> 
        <SearchStocks 
          myStocks={userInfo.stocks} 
          stockService={stockService}
          storeManager={storeManager}/> 
      </>
    }
  </div>;
}

const mapStateToProps = state => {
  return {
    userInfo: state.userInfo
  };
};

export default connect(mapStateToProps)(App);