import StockService, { StockInfo } from "../../services/StockService";
import StoreManager from "../../store/StoreManager";
import SearchResultRow from "./SearchResultRow";

interface Props {
  stocks: StockInfo[];
  myStocks: string[];
  stockService: StockService;
  storeManager: StoreManager;
}

export default function SearchResultsTable(props: Props) {

  const { stocks, myStocks, stockService, storeManager } = props;

  return <table className="search-results-table">
    <tbody>
      {
        (stocks || []).map((stock, i) => 
          <SearchResultRow 
            key={i}
            stock={stock} 
            isFollowing={myStocks.some(symbol => symbol === stock.symbol)} 
            stockService={stockService} 
            storeManager={storeManager}/>
        )
      }
    </tbody>
  </table>
}