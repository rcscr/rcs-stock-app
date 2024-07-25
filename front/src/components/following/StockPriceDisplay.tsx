import { useEffect, useState, useCallback } from "react";
import SockJsClient from 'react-stomp';
import StockService, { StockPrice } from "../../services/StockService";
import { faArrowUp, faArrowDown, faTimes } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import StoreManager from "../../store/StoreManager";
import LoadingText from '../utils/LoadingText';

interface Props {
  symbol: string;
  stockService: StockService;
  storeManager: StoreManager;
}

function formatPrice(x: number) {
  var parts = x.toString().split(".");
  // formats number with commas
  parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  return parts.join(".");
}

export default function StockPriceDisplay(props: Props) {

  const { 
    symbol,
    stockService,
    storeManager
  } = props;

  const [ stock, setStock ] = useState<StockPrice>(undefined);
  const [ stockPriceClass, setStockPriceClass ] = useState<string>('');

  useEffect(() => {
    async function asyncWaitForAnimation() {
      await new Promise(resolve => setTimeout(resolve, 3000));
      if (stockPriceClass) {
        setStockPriceClass('');
      }
    };
    asyncWaitForAnimation();
  }, [ stockPriceClass ]);

  const onUpdate = useCallback((update: StockPrice) => {
    // only set animation if this is an update, not a first time fetch
    if (stock) {
      setStockPriceClass('received-update');
    }
    setStock(update as StockPrice);
  }, [ stock ])

  const handleUnfollow = useCallback(async () => {
    storeManager.setUserInfo(await stockService.unfollowStock(stock.symbol));
  }, [ storeManager, stockService, stock ]);

  return <div className="stock-price-display">
    <SockJsClient 
      url={stockService.getWebsocketUrl()}
      topics={[stockService.getWebsocketTopicUrl(symbol)]}
      onMessage={onUpdate}/>

    {
      stock && 
      <div className="flex-row-center">
        <FontAwesomeIcon 
          className="margin-right unfollow" 
          icon={faTimes} 
          title="unfollow"
          onClick={handleUnfollow}/>

        <div className="margin-right symbol">{ stock.symbol }</div>

        <div className="margin-right currency">{ stock.currency }</div>

        <div className={`flex-row-center ${stock.percentageChange < 0 ? 'down' : 'up'}`}>
          <div className={`margin-right ${stockPriceClass}`}>{ formatPrice(stock.price) }</div>
          
          <FontAwesomeIcon 
            className="margin-right" 
            icon={stock.percentageChange < 0 ? faArrowDown : faArrowUp }/>

          <div>{ stock.percentageChange.toFixed(2) }% </div>
        </div>
      </div>
    }

    {
      !stock &&
      <LoadingText chars={50}/>
    }
  </div>
}