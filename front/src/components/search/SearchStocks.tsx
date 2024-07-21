import { useEffect, useState } from "react";
import StockService, { StockInfo } from "../../services/StockService";
import StoreManager from "../../store/StoreManager";
import Header from "../utils/Header";
import SearchStocksResultsLoading from "./SearchResultsLoading";
import SearchResultsTable from "./SearchResultsTable";

interface Props {
  myStocks: string[];
  stockService: StockService;
  storeManager: StoreManager;
}

export default function SearchStocks(props: Props) {

  const { myStocks, stockService, storeManager } = props;

  const [ search, setSearch ] = useState<string>();
  const [ stocks, setStocks ] = useState<StockInfo[]>(undefined);
  const [ loading, setLoading ] = useState<boolean>();

  useEffect(() => {
    if (search) {
      setLoading(true);
      setStocks([]);

      const debouncedSearch = setTimeout(() => {
        async function fetchAndSetStocks() {
          const stocks = await stockService.searchStocks(search);
          setLoading(false);
          setStocks(stocks);
        }
        fetchAndSetStocks();
      }, 1000)

      return () => {
        clearTimeout(debouncedSearch);
        setLoading(false);
      };
    } else {
      setLoading(false);
      setStocks(undefined);
    }
  }, [ search ])

  return <div id="search">
    <div className="container flex-column">

      <Header 
        title="Discover" 
        subtitle="Search stocks from NYSE and Nasdaq"/>

      <input 
        placeholder="search"
        type="text" 
        value={search} 
        onChange={({ target: { value }}) => setSearch(value)} />

      {
        loading && <SearchStocksResultsLoading/>
      }  

      {
        !loading && stocks?.length === 0 && 
        <div>No results</div>
      }

      <SearchResultsTable 
        stocks={stocks}
        myStocks={myStocks}
        stockService={stockService}
        storeManager={storeManager}/>
    </div>
  </div>;
}