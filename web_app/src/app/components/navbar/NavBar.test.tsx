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
    dict: {
      "dashboard": "Dashboard",
      "devices": "Devices",
      "media": "Media",
      "notifications": "Notifications",
      "settings": "Settings",
      "logout": "Logout",
      "version": "App version"
    }
  };

  // it('renders the logo', () => {
  //   render(<NavBar {...mockProps} />);
  //   const logo = screen.getByAltText('SmartSecurity logo');
  //   expect(logo).toBeInTheDocument();
  // });

  it('renders the title and subtitle links', () => {
    const { container } = render(<NavBar {...mockProps} />);
    
    const dashboardLink = container.querySelector('a[href="/dashboard"]');
    expect(dashboardLink).toBeInTheDocument();
    expect(dashboardLink).toHaveTextContent('Dashboard');

    const subtitleLink = container.querySelector('a[href="/dashboard/overview"]');
    expect(subtitleLink).toBeInTheDocument();
    expect(subtitleLink).toHaveTextContent('Overview');
  });

  // it('toggles the menu when hamburger icon is clicked', () => {
  //   render(<NavBar {...mockProps} />);
  //   const hamburgerIcon = screen.getByRole('button');
  //   fireEvent.click(hamburgerIcon);
  //   const menu = screen.getByTestId('menu');
  //   expect(menu).toHaveClass('menuOpen');
  // });
});