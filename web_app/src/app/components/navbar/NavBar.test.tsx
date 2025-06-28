import { render, fireEvent } from '@testing-library/react';
import NavBar from '@/app/components/navbar/NavBar';
import '@testing-library/jest-dom';

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
      "settings": "Settings",
      "logout": "Logout",
      "version": "App version"
    }
  };

  it('renders the title and subtitle links', () => {
    const { container } = render(<NavBar {...mockProps} />);
    
    const dashboardLink = container.querySelector('a[href="/dashboard"]');
    expect(dashboardLink).toBeInTheDocument();
    expect(dashboardLink).toHaveTextContent('Dashboard');

    const subtitleLink = container.querySelector('a[href="/dashboard/overview"]');
    expect(subtitleLink).toBeInTheDocument();
    expect(subtitleLink).toHaveTextContent('Overview');
  });

  it('toggles the menu when hamburger icon is clicked', () => {
    const { container } = render(<NavBar {...mockProps} />);
    const hamburgerIcon = container.querySelector('.hamburger');

    expect(hamburgerIcon).toBeInTheDocument();

    if (hamburgerIcon) {
      fireEvent.click(hamburgerIcon);
    }

    const menu = container.querySelector(`.menu`);
    expect(menu).toBeInTheDocument();

    expect(menu?.className).toContain('menuOpen');
  });
});