import LoadingText from "../utils/LoadingText";

export default function SearchStocksResultsLoading() {

  return <div className="search-stocks-results-loading">
    {
      Array.from(Array(5).keys()).map(i => 
        <div key={i} className="flex-row">
          <LoadingText chars={10}/>
          <LoadingText chars={20}/>
          <LoadingText chars={50}/>
        </div>
      )
    }
  </div>
}