import { useEffect, useState } from "react";
import StockService, { StockWithFollowers } from "../../services/StockService";
import StoreManager from "../../store/StoreManager";
import Header from "../utils/Header";
import StockWithFollowersDisplay from "./StockWithFollowersDisplay";

interface Props {
  myStocks: string[];
  stockService: StockService;
  storeManager: StoreManager;
}

export default function PopularStocks(props: Props) {

  const { myStocks, stockService, storeManager } = props;

  const [ stocksWithFollowers, setStocksWithFollowers ] = useState<StockWithFollowers[]>([]);

  useEffect(() => {
    async function fetchAndSetStocksWithFollowers() {
      const stocksWithFollowers = await stockService.getStocksWithFollowers();
      setStocksWithFollowers(stocksWithFollowers);
    }
    fetchAndSetStocksWithFollowers();
  }, [ myStocks ]);

  return <div id="popular-stocks">      
    <div className="container">
      <Header 
        title="Popular stocks" 
        subtitle={`Here are the top ${stocksWithFollowers.length} stocks and their number of followers`}/>

      <div className="flex-row">
        {
          stocksWithFollowers.map((stockWithFollowers, i) => 
            <StockWithFollowersDisplay 
              key={i}
              stockWithFollowers={stockWithFollowers} 
              isFollowing={myStocks.some(symbol => symbol === stockWithFollowers.symbol)}
              stockService={stockService}
              storeManager={storeManager}/>
          )
        }
      </div>
    </div>
  </div>
}