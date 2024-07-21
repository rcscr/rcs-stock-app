import LoadingText from "./LoadingText";

interface Props {
  title: string;
  subtitle: string;
}

function Header(props: Props) {
  const { title, subtitle } = props;

  return <div className="page-header">
    <h2>
      { title }
      { !title && <LoadingText chars={25}/> }
    </h2>
    <small>
      { subtitle }
      { !subtitle && <LoadingText chars={20}/> }
    </small>
  </div>
}

export default Header;