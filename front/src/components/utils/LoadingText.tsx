interface Props {
  chars: number;
}

export default function LoadingText(props: Props) {
  return <div>
    <div className="loading-text">
      {
        Array(props.chars).map(entry => '').join()
      }
    </div>
  </div>;
}