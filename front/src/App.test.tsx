import { render, screen } from '@testing-library/react';
import App from './App';
import StoreManager from './store/StoreManager';

test('renders learn react link', () => {
  render(<App storeManager={new StoreManager(undefined)} servicesConfig={{ authServiceUrl: '', stockServiceUrl: '' }}/>);
  const linkElement = screen.getByText(/RCS Stock App/i);
  expect(linkElement).toBeInTheDocument();
});
