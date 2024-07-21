import StockService from "../../services/StockService";
import StoreManager from "../../store/StoreManager";
import Header from "../utils/Header";
import StockPriceDisplay from "./StockPriceDisplay";

interface Props {
  myStocks: string[];
  stockService: StockService;
  storeManager: StoreManager;
}

export default function MyStocks(props: Props) {

  const { myStocks, stockService, storeManager } = props

  return <div id="my-stocks">
    <div className="container">
      <Header 
        title="Stocks I'm following" 
        subtitle="No need to refresh your browser, prices are updated automatically"/>

      <div className="flex-row">
        {
          myStocks.map((symbol, i) => 
            <StockPriceDisplay 
              key={i} 
              symbol={symbol} 
              stockService={stockService}
              storeManager={storeManager}/>
          )
        }
      </div>

      {
        myStocks.length === 0 &&
        <div>
          Looks like you're not following any stocks. Use the search below to discover stocks to follow.
        </div>
      }
    </div>
  </div>
}