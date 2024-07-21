import { useState } from "react";

interface Props {
  title?: string;
  submitLabel: string;
  error?: string;
  success?: string;
  onSubmit: (username, password) => void;
}

export default function Login(props: Props) {

  const [ username, setUsername ] = useState<string>();
  const [ password, setPassword ] = useState<string>();

  const handleSubmit = (event) => {
    event.preventDefault();
    props.onSubmit(username, password);
    setUsername('');
    setPassword('');
  }

  return <div className="user-credentials-form flex-column">
    {
      props.title && 
      <div className="title">
        { props.title }
      </div>
    }
    <div className="flex-row">
      <form className="flex-column" onSubmit={handleSubmit}>
        <input 
          placeholder="username"
          type="text" 
          value={username} 
          onChange={({ target: { value }}) => setUsername(value) } />

        <input 
          placeholder="password"
          type="password" 
          value={password} 
          onChange={({ target: { value }}) => setPassword(value) } />
          
        <input 
          type="submit" 
          value={props.submitLabel} 
          disabled={!username || !password}/>
      </form>
      {
        props.error &&
        <div className="message-error">
          { props.error }
        </div>
      }
      {
        props.success &&
        <div className="message-success">
          { props.success }
        </div>
      }
    </div>
  </div>
}