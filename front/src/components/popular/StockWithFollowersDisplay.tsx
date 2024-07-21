import { faPlus } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import StockService, { StockWithFollowers } from "../../services/StockService";
import StoreManager from "../../store/StoreManager";

interface Props {
  stockWithFollowers: StockWithFollowers;
  isFollowing: boolean;
  stockService: StockService;
  storeManager: StoreManager;
}

export default function StockWithFollowersDisplay(props: Props) {

  const { 
    stockWithFollowers: { symbol, numberOfFollowers }, 
    isFollowing,
    stockService,
    storeManager
   } = props;

  const handleFollow = async () => {
    storeManager.setUserInfo(await stockService.followStock(symbol));
  }

  return <div className="stock-with-followers">
    <FontAwesomeIcon 
      className={`margin-right ${isFollowing ? 'follow-disabled' : 'follow'}`}
      icon={faPlus} 
      title={isFollowing ? "already following" : "follow"} 
      onClick={isFollowing ? undefined : handleFollow}/>
      
    <div className="margin-right symbol">{ symbol }</div>

    <div className="followers">{ numberOfFollowers }</div>
  </div>
}
         