import { render, screen } from '@testing-library/react';
import NavBar from '@/app/components/navbar/NavBar';
import '@testing-library/jest-dom';

// TODO - POPRAWIC TEN TEST

describe('NavBar Component', () => {
  const mockProps = {
    title: 'Dashboard',
    titleUrl: '/dashboard',
    subtitle: 'Overview',
    subtitleUrl: '/dashboard/overview',
  };

  // it('renders the logo', () => {
  //   render(<NavBar {...mockProps} />);
  //   const logo = screen.getByAltText('SmartSecurity logo');
  //   expect(logo).toBeInTheDocument();
  // });

  it('renders the title and subtitle links', () => {
    render(<NavBar {...mockProps} />);
    const titleLink = screen.getByText('Dashboard');
    const subtitleLink = screen.getByText('Overview');
    expect(titleLink).toBeInTheDocument();
    expect(subtitleLink).toBeInTheDocument();
  });

  // it('toggles the menu when hamburger icon is clicked', () => {
  //   render(<NavBar {...mockProps} />);
  //   const hamburgerIcon = screen.getByRole('button');
  //   fireEvent.click(hamburgerIcon);
  //   const menu = screen.getByTestId('menu');
  //   expect(menu).toHaveClass('menuOpen');
  // });
});