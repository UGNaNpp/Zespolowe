import { render, screen, fireEvent } from '@testing-library/react';
import Login from '@/app/components/login/Login';
import '@testing-library/jest-dom';
import { signIn, useSession } from 'next-auth/react';
import { useRouter } from 'next/navigation';

jest.mock('next-auth/react');
jest.mock('next/navigation', () => ({
  useRouter: jest.fn(),
}));

describe('Login component', () => {
  const mockDict = {
    "pageTitle": "Log in",
    "title": "Smart Security",
    "button": "Login with Github"
  };

  const mockPush = jest.fn();

  beforeEach(() => {
    (useRouter as jest.Mock).mockReturnValue({ push: mockPush });
    jest.clearAllMocks();
  });

  it('renders title and login button', () => {
    (useSession as jest.Mock).mockReturnValue({ data: null, status: 'unauthenticated' });

    render(<Login dict={mockDict} />);
    
    expect(screen.getByText('Smart Security')).toBeInTheDocument();
    expect(screen.getByText('Login with Github')).toBeInTheDocument();
    expect(screen.getByAltText('SmartSecurity logo')).toBeInTheDocument();
  });

  it('calls signIn on button click', () => {
    (useSession as jest.Mock).mockReturnValue({ data: null, status: 'unauthenticated' });

    render(<Login dict={mockDict} />);

    const button = screen.getByRole('button', { name: /login with github/i });
    fireEvent.click(button);

    expect(signIn).toHaveBeenCalledWith('github', { callbackUrl: '/', method: 'POST' });
  });

  it('redirects to home if already authenticated', () => {
    (useSession as jest.Mock).mockReturnValue({ data: { user: {} }, status: 'authenticated' });

    render(<Login dict={mockDict} />);
    
    expect(mockPush).toHaveBeenCalledWith('/');
  });
});
