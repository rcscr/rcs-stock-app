import AuthService from "../../services/AuthService";
import StoreManager from "../../store/StoreManager";
import { UserInfo } from "../../store/types";

interface Props {
  userInfo: UserInfo;
  authService: AuthService;
  storeManager: StoreManager
}

function Nav(props: Props) {

  const { userInfo, authService, storeManager } = props;

  const handleLogout = () => {
    authService.logout();
    storeManager.setUserInfo(undefined);
  };

  const isLoggedIn = !!userInfo?.username;

  return <div id="nav">
    <div className="container">
      <div>
        <a className="brand">
          RCS Stocks
        </a>
      </div>
      {
        isLoggedIn &&
        <div className="flex-row">
          <a>
            Logged in as { userInfo.username }
          </a>
          <a className="logout" onClick={handleLogout}>
            logout
          </a>
        </div>
      }
    </div>
  </div>
}

export default Nav;