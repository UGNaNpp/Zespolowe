import { render, screen, fireEvent } from '@testing-library/react';
import NoAccess from '@/app/components/noaccess/NoAccess';
import '@testing-library/jest-dom';
import { useRouter } from 'next/navigation';

jest.mock('next/navigation', () => ({
  useRouter: jest.fn(),
}));

describe('NoAccess component', () => {
  const mockDict = {
    "pageTitle": "No access",
    "title": "No access to service",
    "message": "Try going back to home page, clear cookies or contact the owner.",
    "button": "Go to home page"
  };

  const mockPush = jest.fn();

  beforeEach(() => {
    (useRouter as jest.Mock).mockReturnValue({ push: mockPush });
    jest.clearAllMocks();
  });

  it('renders title, message, button, and footer', () => {
    render(<NoAccess dict={mockDict} />);

    expect(screen.getByText(mockDict.title)).toBeInTheDocument();
    expect(screen.getByText(mockDict.message)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: mockDict.button })).toBeInTheDocument();
    expect(screen.getByText('Smarty Security 2025')).toBeInTheDocument();
  });

  it('navigates to home page when button is clicked', () => {
    render(<NoAccess dict={mockDict} />);

    const button = screen.getByRole('button', { name: mockDict.button });
    fireEvent.click(button);

    expect(mockPush).toHaveBeenCalledWith('/');
  });
});
