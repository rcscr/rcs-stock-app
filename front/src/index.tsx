import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import { Provider } from 'react-redux';
import { createStore } from 'redux';
import rootReducer from './store/reducers';
import StockAppManager from './store/StoreManager';
import './index.scss';
import 'bootstrap/dist/css/bootstrap.min.css';

const servicesConfig = {
  authServiceUrl: process.env.REACT_APP_AUTH_SERVICE_URL,
  stockServiceUrl: process.env.REACT_APP_STOCK_SERVICE_URL
};

const store = createStore(rootReducer);

const manager = new StockAppManager(store);

ReactDOM.render(
  <React.StrictMode>
    <Provider store={store}>
      <App servicesConfig={servicesConfig} storeManager={manager}/>
    </Provider>
  </React.StrictMode>,
  document.getElementById('root')
);