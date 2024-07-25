import { useEffect, useState, useCallback } from "react";
import StoreManager from "../../store/StoreManager";
import AuthService from "../../services/AuthService";
import UserCredentialsForm from "./UserCredentialsForm";
import Header from "../utils/Header";
import StockService from "../../services/StockService";

interface Props {
  authService: AuthService;
  stockService: StockService;
  storeManager: StoreManager;
}

export default function LoginRegister(props: Props) {

  const { authService, stockService, storeManager } = props;

  const [ loginError, setLoginError ] = useState<boolean>(undefined);
  const [ registerError, setRegisterError ] = useState<string>(undefined);
  const [ registerSuccess, setRegisterSuccess ] = useState<string>(undefined);

  useEffect(() => {
    async function fetchAndSetMyStocks() {
      storeManager.setUserInfo(await stockService.getMyStocks());
    }
    if (loginError == false) {
      fetchAndSetMyStocks();
    }
  }, [ loginError ]);

  const handleLogin = useCallback((username, password) => {
    authService.login(username, password)
      .then(success => {
        setLoginError(!success);
      });
  }, [ authService ]);

  const handleRegister = (username, password) => {
    if (password.length < 8) {
      setRegisterError('The password must contain at least 8 characters');
    } else {
      authService.register(username, password)
        .then(response => {
          if (response.ok) {
            setRegisterSuccess(`Welcome, ${username}. You may now login with your credentials`);
            setRegisterError(undefined);
          } else {
            setRegisterSuccess(undefined);
            switch(response.status) {
              case 409: setRegisterError('A user with that username already exists'); break;
              default: setRegisterError('Failed to register');
            }
          }
        });
    }
  }

  return <div id="login-register">
    <div className="container">
      <Header 
        title="Welcome to RCS Stocks" 
        subtitle="Login to follow stocks from NYSE and Nasdaq"/>

      <div className="flex-column">
        <UserCredentialsForm 
          submitLabel="Login" 
          error={loginError && 'Failed to log in'}
          onSubmit={handleLogin}/>

        <UserCredentialsForm 
          title="Not yet a user?"
          submitLabel="Register" 
          error={registerError}
          success={registerSuccess}
          onSubmit={handleRegister}/>
      </div>
    </div>
  </div>
}