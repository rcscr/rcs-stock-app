import StockService, { StockInfo } from "../../services/StockService";
import { faPlus } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import StoreManager from "../../store/StoreManager";

interface Props {
  stock: StockInfo;
  isFollowing: boolean;
  stockService: StockService;
  storeManager: StoreManager;
}

export default function SearchResultRow(props: Props) {

  const { 
    stock: { symbol, description }, 
    isFollowing,
    stockService, 
    storeManager 
  } = props;

  const handleFollow = async () => {
    storeManager.setUserInfo(await stockService.followStock(symbol));
  }

  return <tr className="search-result-row">
    <td>
      <FontAwesomeIcon 
        className={isFollowing ? 'follow-disabled' : 'follow'}
        icon={faPlus} 
        title={isFollowing ? "already following" : "follow"} 
        onClick={isFollowing ? undefined : handleFollow}/>
    </td>
    <td className="symbol">
      { symbol }
    </td>
    <td className="ellipsis">
      { description }
    </td>
  </tr>
}